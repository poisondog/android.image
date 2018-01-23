/*
 * Copyright (C) 2013 Adam Huang <poisondog@gmail.com>
 * Copyright (C) 2014 Zi-Xiang Lin <bdl9437@gmail.com>
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

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import java.util.List;

/**
 * This is a class of inherit FragmentStatePagerAdapter. 
 * <p>
 * This usage is set to ViewPager after it instantiates.
 * @author Adam Huang <poisondog@gmail.com>
 */
public class PhotoPageAdapter extends FragmentStatePagerAdapter {
	private List<String> mUrls;

	public PhotoPageAdapter(FragmentManager fm, List<String> urls) {
		super(fm);
		mUrls = urls;
	}

	@Override
	public int getCount() {
		return mUrls.size();
	}

	@Override
	public Fragment getItem(int position) {
		PhotoFragment fragment = new PhotoFragment();
		fragment.setPath(mUrls.get(position));
		return fragment;
	}
	
	@Override
	public void destroyItem(View collection, int position, Object o) {
		View view = (View)o;
		((ViewPager) collection).removeView(view);
		view = null;
		System.gc();
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}
}
