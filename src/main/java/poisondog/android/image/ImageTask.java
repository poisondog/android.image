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
import android.widget.ImageView;
import poisondog.android.os.AsyncTask;
import poisondog.core.Mission;
import poisondog.core.NoMission;

public abstract class ImageTask implements Mission<ImageParameter> {
	private boolean mExitTasksEarly = false;
	private boolean mPauseWork = false;
	private final Object mPauseWorkLock = new Object();
	private Resources mResources;
	private ImageCache mImageCache;
	private Bitmap mLoadingBitmap;
	private Mission<Object> mHandler;

	protected ImageTask(Context context) {
		mResources = context.getResources();
		mImageCache = new ImageCache(context, context.getExternalCacheDir().getPath() + "/");
		mHandler = new NoMission<>();
//		mLoadingBitmap = BitmapFactory.decodeResource(mResources, R.drawable.image_loading);
	}

	public void setHandler(Mission<Object> handler) {
		mHandler = handler;
	}

	protected abstract Bitmap processBitmap(Object data);

	@Override
	public Void execute(ImageParameter para) {
		Object data = para.getData();
		ImageView imageView = para.getView();
		if (data == null) {
			return null;
		}
//		CancelPotentialWork mission = new CancelPotentialWork();
//		if (mission.execute(data, imageView)) {
			final ImageAsyncTask async = new ImageAsyncTask(this, imageView);
			async.setHandler(mHandler);
			imageView.setImageDrawable(new AsyncDrawable(mResources, mLoadingBitmap, async));
			async.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, data);
//		}
		return null;
	}

	public void loadImage(Object data, ImageView imageView) {
		execute(new ImageParameter(data, imageView));
	}

	public void setPauseWork(boolean pauseWork) {
		synchronized (mPauseWorkLock) {
			mPauseWork = pauseWork;
			if (!mPauseWork) {
				mPauseWorkLock.notifyAll();
			}
		}
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
