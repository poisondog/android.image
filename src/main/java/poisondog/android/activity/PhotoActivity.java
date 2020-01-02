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
package poisondog.android.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import java.util.List;
import poisondog.android.image.ImageFetcher;
import poisondog.android.image.R;
import poisondog.android.pager.PageAdapter;
import poisondog.android.pager.PageView;
import poisondog.android.util.GetDisplaySize;
import poisondog.android.util.GetExternalCacheFolder;
import poisondog.android.view.LoadingView;
import poisondog.core.Mission;
import poisondog.vfs.FileFactory;
import poisondog.vfs.IData;

/**
 * @author Adam Huang
 * @since 2018-01-24
 */
public class PhotoActivity extends Activity {
	public static final String PHOTOS = "content";
	public static final String TARGET = "target";
	public static final String POSITION = "position";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_photo);

		List<Object> myList = (List<Object>) getIntent().getSerializableExtra(PHOTOS);
		final String target = getIntent().getExtras().getString(TARGET);
		final int position = getIntent().getExtras().getInt(POSITION);

		PageView page = (PageView)findViewById(R.id.image_page);
		final PageAdapter adapter = new PageAdapter();
		adapter.setViewFactory(new ImageViewFactory());
		adapter.setContent(myList);
		page.setAdapter(adapter);

		if (target != null) {
			for (int i = 0; i < myList.size(); i++) {
				if (myList.get(i).toString().equals(target)) {
					page.setCurrentIndex(i);
				}
			}
		} else {
			page.setCurrentIndex(position);
		}
	}

	class ImageViewFactory implements Mission<Object> {
		private GetDisplaySize.Size mSize;
		private ImageFetcher mFetcher;
		/**
		 * Constructor
		 */
		public ImageViewFactory() {
			GetDisplaySize task = new GetDisplaySize();
			mSize = task.execute(PhotoActivity.this);
			try{
				String cache = new GetExternalCacheFolder().execute(PhotoActivity.this);
				mFetcher = new ImageFetcher(PhotoActivity.this, mSize.getWidth(), mSize.getHeight(), cache);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		@Override
		public View execute(Object url) throws Exception {
			ImageView image = new ImageView(PhotoActivity.this);
			image.setLayoutParams(new ViewGroup.LayoutParams(mSize.getWidth(), mSize.getHeight()));
			LoadingView loading = new LoadingView(PhotoActivity.this);
			loading.setContent(image);
			loading.setLoading(true);
//			mFetcher.setCopyFactory(new CustomCopyFactory((IData) FileFactory.getFile(url.toString())));
			mFetcher.setHandler(loading.stopMission());
			mFetcher.loadImage(url.toString(), image);
			return loading;
		}
	}

}
