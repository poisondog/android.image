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
import android.graphics.Bitmap;
import poisondog.net.URLUtils;
/**
 * @author poisondog <poisondog@gmail.com>
 */
public class ImageResize extends ImageTask {
	private int reqWidth;
	private int reqHeight;

	public ImageResize(Context context, int reqWidth, int reqHeight) {
		super(context);
		this.reqWidth = reqWidth;
		this.reqHeight = reqHeight;
	}

	protected Bitmap processBitmap(Object data){
		if(!(data instanceof String))
			return null;
		String url = (String)data;
		if(!URLUtils.scheme(url).equals("file")) {
			return null;
		}
		return ImageUtil.resize(URLUtils.path(url), reqWidth, reqHeight);
	}
}
