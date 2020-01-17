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
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import java.util.List;
import poisondog.android.image.app.R;
import poisondog.android.image.ImageDiskCache;
import poisondog.android.image.ImageFetcher;
import poisondog.android.image.ImageLoader;
import poisondog.android.image.ImagePara;
import poisondog.android.image.ImageUtil;
import poisondog.android.image.ImageWorker;
import poisondog.android.pager.PageAdapter;
import poisondog.android.pager.PageView;
import poisondog.android.util.GetDisplaySize;
import poisondog.android.util.GetExternalCacheFolder;
import poisondog.cache.MissionCache;
import poisondog.core.Mission;
import poisondog.net.UrlUtils;

/**
 * @author Adam Huang
 * @since 2018-01-24
 */
public class PhotoActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo);

		List<Object> myList = (List<Object>) getIntent().getSerializableExtra("content");
		PageView page = (PageView)findViewById(R.id.page);
		PageAdapter adapter = new PageAdapter();
		adapter.setViewFactory(new ImageViewFactory());
		adapter.setContent(myList);
		page.setAdapter(adapter);
		page.setCurrentIndex(getIntent().getExtras().getInt("current"));
	}

	class ImageViewFactory implements Mission<Object> {
		private GetDisplaySize.Size mSize;
//		private ImageFetcher mFetcher;
		private ImageLoader mLoader;
		private ImageWorker mBinder;
		/**
		 * Constructor
		 */
		public ImageViewFactory() {
			GetDisplaySize task = new GetDisplaySize();
			mSize = task.execute(PhotoActivity.this);
			try{
				String cache = new GetExternalCacheFolder().execute(PhotoActivity.this);
//				mFetcher = new ImageFetcher(PhotoActivity.this, mSize.getWidth(), mSize.getHeight(), cache);

				mLoader = new ImageLoader(500, 500, cache);
				MissionCache mc = new MissionCache(mLoader);
				mc.setCache(ImageDiskCache.open(cache));
				mBinder = new ImageWorker(mc);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		@Override
		public ImageView execute(Object url) throws Exception {
			ImageView image = new ImageView(PhotoActivity.this);
			image.setLayoutParams(new ViewGroup.LayoutParams(mSize.getWidth(), mSize.getHeight()));
//			mFetcher.loadImage(url.toString(), image);
			mBinder.execute(new ImagePara(url.toString(), image));
//			System.out.println(ImageUtil.getBitmapDegree(UrlUtils.path(url.toString())));
			return image;
		}
	}


}
