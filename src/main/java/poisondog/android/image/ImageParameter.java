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

import android.widget.ImageView;

/**
 * @author Adam Huang
 * @since 2020-01-07
 */
public class ImageParameter {
	private Object mData;
	private ImageView mView;

	/**
	 * Constructor
	 */
	public ImageParameter(Object data, ImageView imageView) {
		mData = data;
		mView = imageView;
	}

	public Object getData() {
		return mData;
	}

	public ImageView getView() {
		return mView;
	}

}
