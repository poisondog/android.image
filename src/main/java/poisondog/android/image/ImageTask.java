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

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import poisondog.android.os.AsyncTask;

/**
 * @author poisondog <poisondog@gmail.com>
 */
public abstract class ImageTask {
	private boolean mExitTasksEarly = false;
	private boolean mPauseWork = false;
	private final Object mPauseWorkLock = new Object();
	private Resources mResources;
	private ImageCache mImageCache;
	private Bitmap mLoadingBitmap;

	protected ImageTask(Context context) {
		mResources = context.getResources();
	}

	protected abstract Bitmap processBitmap(Object data);

	public void loadImage(Object data, ImageView imageView) {
		if (data == null) {
			return;
		}

		if (cancelPotentialWork(data, imageView)) {
			final ImageAsyncTask task = new ImageAsyncTask(this, imageView);
			imageView.setImageDrawable(new AsyncDrawable(mResources, mLoadingBitmap, task));
//			task.execute(data);
			task.executeOnExecutor(AsyncTask.DUAL_THREAD_EXECUTOR, data);
		}
	}

	public static boolean cancelPotentialWork(Object data, ImageView imageView) {
		final ImageAsyncTask task = getImageAsyncTask(imageView);
		if (task != null) {
			final Object bitmapData = task.getData();
			if (bitmapData == null || !bitmapData.equals(data)) {
				task.cancel(true);
			} else {
				return false;
			}
		}
		return true;
	}

	public static ImageAsyncTask getImageAsyncTask(ImageView imageView) {
		if (imageView != null) {
			final Drawable drawable = imageView.getDrawable();
			if (drawable instanceof AsyncDrawable) {
				final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
				return asyncDrawable.getImageAsyncTask();
			}
		}
		return null;
	}

	public void setLoadingImage(Bitmap bitmap) {
		mLoadingBitmap = bitmap;
	}

	public void setLoadingImage(int resId) {
		mLoadingBitmap = BitmapFactory.decodeResource(mResources, resId);
	}

	public void setImageCache(ImageCache imageCache) {
		mImageCache = imageCache;
	}

	public void setPauseWork(boolean pauseWork) {
		synchronized (mPauseWorkLock) {
			mPauseWork = pauseWork;
			if (!mPauseWork) {
				mPauseWorkLock.notifyAll();
			}
		}
	}

	public ImageCache getImageCache() {
		return mImageCache;
	}

	public Resources getResources() {
		return mResources;
	}

	public Object getPauseWorkLock() {
		return mPauseWorkLock;
	}

	public boolean isPauseWork() {
		return mPauseWork;
	}

	public boolean isExitTasksEarly() {
		return mExitTasksEarly;
	}
}
