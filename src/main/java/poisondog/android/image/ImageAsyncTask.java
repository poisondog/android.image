/*
 * Copyright (C) 2013 Google Inc.
 * Copyright (C) 2013 Adam Huang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package poisondog.android.image;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;
import java.lang.ref.WeakReference;
import poisondog.android.os.AsyncTask;
import poisondog.android.image.R;

/**
 * @author poisondog <poisondog@gmail.com>
 */
public class ImageAsyncTask extends AsyncTask<Object, Void, BitmapDrawable> {
	private Object mData;
	private final ImageTask mTask;
	private final WeakReference<ImageView> mImageViewReference;

	public ImageAsyncTask(ImageTask task, ImageView imageView) {
		mTask = task;
		mImageViewReference = new WeakReference<ImageView>(imageView);
	}

	/**
	 * Background processing.
	 */
	@Override
	protected BitmapDrawable doInBackground(Object... params) {
		mData = params[0];
		final String dataString = String.valueOf(mData);
		Bitmap bitmap = null;
		BitmapDrawable drawable = null;

//		synchronized (mTask.mPauseWorkLock) {
//			System.out.println("::::pause work lock::::");
//			while (mTask.isPauseWork() && !isCancelled()) {
//				System.out.println("::::pause work::::");
//				try {
//					mTask.mPauseWorkLock.wait();
//				} catch (InterruptedException e) {}
//			}
//		}

		if (mTask.getImageCache() != null && !isCancelled() && getAttachedImageView() != null && !mTask.isExitTasksEarly()) {
			try{
				bitmap = mTask.getImageCache().getBitmapFromCache(dataString);
			}catch(Exception e) {
			}
		}

		if (bitmap == null && !isCancelled() && getAttachedImageView() != null && !mTask.isExitTasksEarly()) {
			bitmap = mTask.processBitmap(params[0]);
		}

		if (bitmap != null) {
			drawable = new RecyclingBitmapDrawable(mTask.getResources(), bitmap);
			if (mTask.getImageCache() != null) {
				try{
					mTask.getImageCache().addBitmapToCache(dataString, drawable);
				}catch(Exception e) {
				}
			}
		}

		System.gc();
		return drawable;
	}

	/**
	 * Once the image is processed, associates it to the imageView
	 */
	@Override
	protected void onPostExecute(BitmapDrawable value) {
		// if cancel was called on this task or the "exit early" flag is set then we're done
		if (isCancelled() || mTask.isExitTasksEarly()) {
			value = null;
		}

		final ImageView imageView = getAttachedImageView();
		if (imageView == null)
			return;
		if (value != null) {
			imageView.setImageDrawable(value);
		} else {
			imageView.setImageResource(R.drawable.alert);
		}
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		synchronized (mTask.getPauseWorkLock()) {
			mTask.getPauseWorkLock().notifyAll();
		}
	}

	/**
	 * Returns the ImageView associated with this task as long as the ImageView's task still
	 * points to this task as well. Returns null otherwise.
	 */
	private ImageView getAttachedImageView() {
		GetImageAsyncTask mission = new GetImageAsyncTask();
		final ImageView imageView = mImageViewReference.get();
		final ImageAsyncTask task = mission.execute(imageView);

		if (this == task) {
			return imageView;
		}

		return null;
	}

	public Object getData() {
		return mData;
	}
}
