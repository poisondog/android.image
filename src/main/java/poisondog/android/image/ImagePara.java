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
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import poisondog.core.Mission;

/**
 * @author Adam Huang
 * @since 2020-01-15
 */
public class ImagePara {
	private Object mData;
	private ImageView mView;
	private Mission<Object> mMission;
	private Bitmap mLoadingBitmap;

	/**
	 * Constructor
	 */
	public ImagePara(Object data, ImageView imageView, Mission<Object> mission) {
		mData = data;
		mView = imageView;
		mMission = mission;
//		mLoadingBitmap = BitmapFactory.decodeResource(mView.getContext().getResources(), R.drawable.image_loading);
	}

	public Object getData() {
		return mData;
	}

	public ImageView getView() {
		return mView;
	}

	public Mission<Object> getMission() {
		return mMission;
	}

	public Bitmap getLoadingBitmap() {
		return mLoadingBitmap;
	}

	public void setLoadingImage(Bitmap bitmap) {
		mLoadingBitmap = bitmap;
	}

	public void setLoadingImage(int resId) {
		mLoadingBitmap = BitmapFactory.decodeResource(mView.getContext().getResources(), resId);
	}

}
