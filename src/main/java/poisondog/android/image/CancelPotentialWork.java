/*
 * Copyright (C) 2018 Adam Huang <poisondog@gmail.com>
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
import poisondog.core.Mission;

/**
 * @author Adam Huang
 * @since 2018-01-10
 */
public class CancelPotentialWork implements Mission<Object[]> {

	/**
	 * para[0] is Object class
	 * para[1] is ImageView class
	 */
	@Override
	public Boolean execute(Object... para) {
		if (para.length != 2)
			throw new IllegalArgumentException("input need two parameter, Object and ImageView");
		if (!(para[1] instanceof ImageView))
			throw new IllegalArgumentException("second parameter need ImageView class");
		Object data = para[0];
		ImageView imageView = (ImageView)para[1];
		final ImageAsyncTask async = ImageUtil.getImageAsyncTask(imageView);
		if (async != null) {
			final Object bitmapData = async.getData();
			if (bitmapData == null || !bitmapData.equals(data)) {
				async.cancel(true);
				System.out.println("Cancel");
			} else {
				return false;
			}
		}
		return true;
	}
}
