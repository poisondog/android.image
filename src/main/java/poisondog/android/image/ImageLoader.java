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
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import poisondog.core.Mission;
import poisondog.io.CopyFactory;
import poisondog.io.CopyTask;
import poisondog.log.Log;
import poisondog.net.UrlUtils;
import poisondog.util.Pair;
import poisondog.vfs.FileFactory;
import poisondog.vfs.IData;
import poisondog.vfs.IFile;
import poisondog.vfs.IFolder;
import poisondog.concurrent.Cancellable;

/**
 * @author Adam Huang
 * @since 2020-01-08
 */
public class ImageLoader implements ImageMission {
	private IFolder mDest;
	private CopyFactory mFactory;
	private ImageScale mScale;
	private CopyTask mTask;

	public ImageLoader(int imageWidth, int imageHeight, String destUrl) throws Exception {
		mScale = new ImageScale(imageWidth, imageHeight);
		setDestination(destUrl);
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
		InputStream result = data.getInputStream();
//		Log.d("Width: " + ImageUtil.getWidth(result));
		return result;
	}

	private OutputStream getOutputStream(String url) throws Exception {
		IData data = (IData) FileFactory.getFile(url);
		return data.getOutputStream();
	}

	@Override
	public Bitmap execute(Object data) {
		if(!(data instanceof String))
			return null;
		String url = (String)data;
		Log.i("ImageFetcher URL: " + url);
		if(UrlUtils.scheme(url).equals("file")) {
			return mScale.execute(url);
		}
		try{
			mTask = mFactory.execute(new Pair(getInputStream(url), getOutputStream(mDest.getUrl() + UrlUtils.filename(url))));
//			mTask.transport();
			while(mTask.stepWrite() && !mTask.isCancelled());

			return mScale.execute(mDest.getUrl() + UrlUtils.filename(url));
		}catch(IOException e) {
			e.printStackTrace();
		}catch(Exception e) {
			e.printStackTrace();
		} finally {
			if (mTask == null)
				return null;
			try {
				mTask.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		mTask.cancel();
		return true;
	}

}
