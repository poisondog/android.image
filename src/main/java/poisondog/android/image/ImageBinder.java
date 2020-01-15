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

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;
import poisondog.android.os.AsyncMissionTask;
import poisondog.android.os.AsyncTask;
import poisondog.core.Mission;
import poisondog.core.NoMission;

/**
 * @author Adam Huang
 * @since 2020-01-13
 */
public class ImageBinder implements Mission<ImagePara> {
	private Mission<BitmapDrawable> mHandler;

	/**
	 * Constructor
	 */
	public ImageBinder() {
		mHandler = new NoMission<BitmapDrawable>();
	}

	public void setHandler(Mission<BitmapDrawable> handler) {
		mHandler = handler;
	}

	@Override
	public Void execute(ImagePara para) {
		Object data = para.getData();
		final ImageView imageView = para.getView();
		final ImageMission mission = para.getMission();
		if (mission == null) {
			return null;
		}
		CancelPotentialMission cpm = new CancelPotentialMission();
		if (cpm.execute(data, imageView)) {
			imageView.setImageDrawable(new MissionDrawable(imageView.getContext().getResources(), para.getLoadingBitmap(), data, mission));

//			AsyncMissionTask task = new AsyncMissionTask(mission, mHandler);
//			task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, data);

			AsyncMissionTask task = new AsyncMissionTask(new Mission<Object>() {
				@Override
				public BitmapDrawable execute(Object data) throws Exception {
					return new RecyclingBitmapDrawable(imageView.getContext().getResources(), (Bitmap)mission.execute(data));
				}
			}, new Mission<BitmapDrawable>() {
				@Override
				public Void execute(BitmapDrawable bitmap) throws Exception {
					mHandler.execute(bitmap);
					return null;
				}
			});
			task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, data);

		}
		return null;
	}

}
