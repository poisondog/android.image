/*
 * Copyright (C) 2020 Adam Huang <poisondog@gmail.com>
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
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import poisondog.android.os.AsyncMissionTask;
import poisondog.android.os.AsyncTask;
import poisondog.concurrent.ThreadPool;
import poisondog.core.Mission;
import poisondog.core.NoMission;

/**
 * @author Adam Huang
 * @since 2020-01-13
 */
public class ImageWorker implements Mission<ImagePara> {
	private Mission<Object> mMission;
	private Mission<BitmapDrawable> mHandler;
	private Mission<Object> mCancel;
	private Executor mExecutor;
//	private final Object mPauseWorkLock = new Object();
	private Boolean mPauseWork = false;

	/**
	 * Constructor
	 */
	public ImageWorker(Mission<Object> mission) {
		mMission = mission;
		mHandler = new NoMission<BitmapDrawable>();
		mCancel = new NoMission<Object>();
		mExecutor = AsyncTask.THREAD_POOL_EXECUTOR;
//		mExecutor = new ThreadPool().getExecutor();
//		mExecutor = new ThreadPoolExecutor(2, 2, 60000L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
//		mExecutor = new ThreadPoolExecutor(2, 2, 60000L, TimeUnit.MILLISECONDS, new SynchronousQueue<Runnable>());
	}

	public void setHandler(Mission<BitmapDrawable> handler) {
		mHandler = handler;
	}

	public void setCancel(Mission<Object> cancel) {
		mCancel = cancel;
	}

	public void setExecutor(Executor executor) {
		mExecutor = executor;
	}

	@Override
	public Void execute(ImagePara para) {
		Object data = para.getData();
		ImageView imageView = para.getView();
		if (data == null || imageView == null) {
			return null;
		}
//		final Mission<Object> mission = para.getMission();

		CancelPotentialMission cpm = new CancelPotentialMission();
		if (cpm.execute(data, imageView)) {
			AsyncMissionTask task = new AsyncMissionTask(new ProcessBitmap(imageView.getContext()), new UpdateImageView(imageView));
			task.setCancelMission(mCancel);
			imageView.setImageDrawable(new MissionDrawable(imageView.getContext().getResources(), para.getLoadingBitmap(), data, task));
			task.executeOnExecutor(mExecutor, ImageUtil.getImageObject(imageView));

//			try {
//				Thread.sleep(10L);
//			} catch(Exception e) {
//				e.printStackTrace();
//			}
		}
		return null;
	}

	class ProcessBitmap implements Mission<Object> {
		private Context mContext;
		/**
		 * Constructor
		 */
		public ProcessBitmap(Context context) {
			mContext = context;
		}
		@Override
		public BitmapDrawable execute(Object data) throws Exception {
//			if (!image.isShown())
//				return null;

//			System.out.println("go");
//			synchronized (mPauseWork) {
//				try {
//					while (mPauseWork)
//						mPauseWork.wait();
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//				mPauseWork = true;
//				Object data = ImageUtil.getImageObject(image);
				RecyclingBitmapDrawable result = new RecyclingBitmapDrawable(mContext.getResources(), (Bitmap)mMission.execute(data));
//				mPauseWork = false;
//				mPauseWork.notifyAll();
				return result;
//			}

		}
	}

	class UpdateImageView implements Mission<BitmapDrawable> {
		private ImageView imageView;
		public UpdateImageView(ImageView image) {
			imageView = image;
		}
		@Override
		public Void execute(BitmapDrawable bitmap) throws Exception {
			mHandler.execute(bitmap);
			if (bitmap != null) {
				imageView.setImageDrawable(bitmap);
//			} else {
//				imageView.setImageResource(R.drawable.alert);
			}
			return null;
		}
	}

}
