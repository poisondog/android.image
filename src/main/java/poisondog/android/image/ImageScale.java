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
import poisondog.core.Mission;
import poisondog.net.UrlUtils;

/**
 * @author poisondog <poisondog@gmail.com>
 */
public class ImageScale implements ImageMission {
	private int reqWidth;
	private int reqHeight;

	public ImageScale(int reqWidth, int reqHeight) {
		this.reqWidth = reqWidth;
		this.reqHeight = reqHeight;
	}

	@Override
	public Bitmap execute(Object data) {
		if(!(data instanceof String))
			return null;
		String url = (String)data;
		if(!UrlUtils.scheme(url).equals("file")) {
			return null;
		}
//		return ImageUtil.resize(UrlUtils.path(url), reqWidth, reqHeight);
		Bitmap bitmap = ImageUtil.resize(UrlUtils.path(url), reqWidth, reqHeight);
//		try {
//			return ImageUtil.applyOrientation(bitmap, ImageUtil.resolveBitmapOrientation(UrlUtils.path(url)));
//		} catch(Exception e) {
//			e.printStackTrace();
//		}
		return bitmap;
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return true;
	}

}
