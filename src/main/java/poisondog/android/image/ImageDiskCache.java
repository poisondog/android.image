/*
 * Copyright (C) 2013 Google Inc.
 * Copyright (C) 2013 Adam Huang <poisondog@gmail.com>
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
import java.io.IOException;
import java.io.OutputStream;
import poisondog.commons.HashFunction;
import poisondog.net.URLUtils;
import poisondog.vfs.FileFactory;
import poisondog.vfs.IData;
import poisondog.vfs.IFile;
import poisondog.vfs.IFolder;

public class ImageDiskCache {
	private static final String MESSAGE_DIGEST_ALGORITHM = "MD5";
	private static ImageDiskCache instance;
	private IFolder mCache;

	private ImageDiskCache(IFolder folder) {
		mCache = folder;
	}

	public synchronized static ImageDiskCache open(String folder, int size) throws Exception {
		if(instance != null)
			return instance;
		IFile file = FileFactory.getFile(folder);
		if (!(file instanceof IFolder))
			throw new IllegalArgumentException("this input need folder");
		instance = new ImageDiskCache((IFolder)file);
		return instance;
	}

	private synchronized String getPath(String key) throws Exception {
		return URLUtils.path(mCache.getUrl() + HashFunction.md5(key));
	}

	public synchronized Bitmap get(String key) throws Exception {
		return BitmapFactory.decodeFile(getPath(key), new BitmapFactory.Options());
	}

	public synchronized void put(String key, Bitmap value) throws IOException, Exception {
		IData data = (IData)FileFactory.getFile(getPath(key));
		data.create();
		OutputStream output = data.getOutputStream();
		value.compress(Bitmap.CompressFormat.JPEG, 90, output);
		output.close();
	}
}
