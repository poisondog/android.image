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
package poisondog.android.image.app;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import poisondog.android.image.app.R;
import poisondog.android.image.ImageFetcher;
import poisondog.android.image.ImageLoader;
import poisondog.android.image.ImageBinder;
import poisondog.android.image.ImagePara;
import poisondog.android.image.ImageDiskCache;
import poisondog.android.log.AndroidLogger;
import poisondog.android.util.GetDownloadFolder;
import poisondog.android.util.GetExternalCacheFolder;
import poisondog.log.Log;
import poisondog.vfs.FileFactory;
import poisondog.vfs.filter.FileFilter;
import poisondog.vfs.filter.OnlyImage;
import poisondog.vfs.IData;
import poisondog.vfs.IFile;
import poisondog.vfs.IFolder;

/**
 * @author Adam Huang
 * @since 2018-01-10
 */
public class MainActivity extends Activity {
	private DialogInterface.OnClickListener mListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Log.setSimpleClass(true);
		Log.setLogger(new AndroidLogger("MySkyBox"));

		MyAdapter adapter = new MyAdapter(this);

		GetDownloadFolder task = new GetDownloadFolder();
		String download = task.execute(null);
		try {
			IFolder mFolder = (IFolder)FileFactory.getFile(download);
			FileFilter filter = new FileFilter();
			filter.setIncludeRule(new OnlyImage());
			Collection<IFile> result = filter.execute(mFolder.getChildren());
			adapter.setContent(new LinkedList(result));
		} catch(Exception e) {
			e.printStackTrace();
		}

		GridView gridView = (GridView) findViewById(R.id.content);
		gridView.setNumColumns(3);
		gridView.setAdapter(adapter);

	}

	class MyAdapter extends BaseAdapter{
		private Context mContext;
		private List<IData> mContent;
		private ImageFetcher mFetcher;
		private ImageLoader mLoader;
		private ImageBinder mBinder;
		private String cache;
		/**
		 * Constructor
		 */
		public MyAdapter(Context context) {
			mContext = context;
			mContent = new LinkedList<IData>();
			cache = new GetExternalCacheFolder().execute(mContext);
			try {
				mFetcher = new ImageFetcher(mContext, 500, 500, cache);
//				MissionCache mc = new MissionCache(new ImageLoader(500, 500, cache));
//				mc.setCache(ImageDiskCache.open(cache, 100));
//				mLoader = new ImageLoader(500, 500, cache);
				mBinder = new ImageBinder();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		@Override
		public int getCount() {
			return mContent.size();
		}
		@Override
		public IData getItem(int position) {
			return mContent.get(position);
		}
		@Override
		public long getItemId(int position) {
			return position;
		}
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ImageView image = new ImageView(mContext);
			image.setLayoutParams(new ViewGroup.LayoutParams(500, 500));
			image.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.setClass(MainActivity.this, PhotoActivity.class);
					LinkedList<String> datas = new LinkedList<String>();
					for (IData d : mContent) {
						try {
							datas.add(d.getUrl());
						} catch(Exception e) {
						}
					}
					intent.putExtra("content", datas);
					intent.putExtra("current", position);
					startActivity(intent);
				}
			});
			try {
	//			mFetcher.setLoadingImage(R.drawable.image_loading);
				mFetcher.loadImage(getItem(position).getUrl(), image);
//				mBinder.execute(new ImagePara(getItem(position).getUrl(), image, new ImageLoader(500, 500, cache)));
			} catch(Exception e) {
				e.printStackTrace();
			}
			return image;
		}

		public void setContent(List<IData> content) {
			mContent = content;
			notifyDataSetChanged();
		}
	}
}
