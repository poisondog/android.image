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

import android.content.Context;
import android.graphics.Bitmap;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import poisondog.io.CopyFactory;
import poisondog.io.CopyTask;
import poisondog.log.Log;
import poisondog.net.URLUtils;
import poisondog.util.Pair;
import poisondog.vfs.FileFactory;
import poisondog.vfs.IData;
import poisondog.vfs.IFile;
import poisondog.vfs.IFolder;

/**
 * @author Adam Huang
 * @since 2013-01-10
 */
public class ImageFetcher extends ImageResize {
	private IFolder mDest;
	private CopyFactory mFactory;

	public ImageFetcher(Context context, int imageWidth, int imageHeight, String dest) throws Exception {
		super(context, imageWidth, imageHeight);
		setDestination(dest);
		mFactory = new CopyFactory();
	}

	public void setDestination(String url) throws Exception {
		IFile file = FileFactory.getFile(url);
		if (!(file instanceof IFolder))
			throw new IllegalArgumentException("the destination need folder.");
		mDest = (IFolder) file;
	}

	public void setCopyFactory(CopyFactory factory) {
		mFactory = factory;
	}

	private InputStream getInputStream(String url) throws Exception {
		IData data = (IData) FileFactory.getFile(url);
		return data.getInputStream();
	}

	private OutputStream getOutputStream(String url) throws Exception {
		IData data = (IData) FileFactory.getFile(url);
		return data.getOutputStream();
	}

	@Override
	protected Bitmap processBitmap(Object data){
		if(!(data instanceof String))
			return null;
		String url = (String)data;
		Log.i("ImageFetcher URL: " + url);
		if(URLUtils.scheme(url).equals("file")) {
			return super.processBitmap(url);
		}
		CopyTask task = null;
		try{
			task = mFactory.execute(new Pair(getInputStream(url), getOutputStream(mDest.getUrl() + URLUtils.file(url))));
			task.transport();
			return super.processBitmap(mDest.getUrl() + URLUtils.file(url));
		}catch(IOException e) {
			e.printStackTrace();
		}catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {
				task.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
