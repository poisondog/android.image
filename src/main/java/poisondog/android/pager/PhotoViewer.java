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
package poisondog.android.pager;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import java.util.ArrayList;
import java.util.List;
import poisondog.android.image.R;

/**
 * @author Adam Huang
 * @since 2018-01-23
 */
public class PhotoViewer extends RelativeLayout {
	private Context mContext;
	private ViewPager mPager;
	private PhotoPageAdapter mPagerAdapter;
	private List<String> mPaths;
	private List<ViewPager.OnPageChangeListener> mOnPageChangeListeners;

	/**
	 * Constructor
	 */
	public PhotoViewer(Context context) {
		super(context);
		mContext = context;
	}

	/**
	 * Constructor
	 */
	public PhotoViewer(Context context, AttributeSet set) {
		super(context, set);
		mContext = context;
	}

	private void initial() {
		LayoutInflater.from(mContext).inflate(R.layout.photo_view, this);
		mPager = (ViewPager)findViewById(R.id.photo_pager);
		mPaths = new ArrayList<String>();
		addView(mPager);
	}

	public void setContent(FragmentManager manager, List<String> content, int index) {
		mPaths = content;
		mPagerAdapter = new PhotoPageAdapter(manager, mPaths);
		mPager.setAdapter(mPagerAdapter);
		mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
			public void onPageScrolled (int position, float positionOffset, int positionOffsetPixels){
				for(ViewPager.OnPageChangeListener listener : mOnPageChangeListeners)
					listener.onPageSelected(position);
			}
		});
		mPager.setCurrentItem(index);
	}
}
