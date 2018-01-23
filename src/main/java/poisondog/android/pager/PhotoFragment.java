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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import poisondog.android.image.R;
import poisondog.android.image.ImageCache;
import poisondog.android.image.ImageFetcher;
import poisondog.android.image.RecyclingBitmapDrawable;

/**
 * This class is a page of show photo.
 * <p>
 * This used to getItem() function in the FragmentStatePagerAdapter,
 * and must be use setPath function to set path of photo.
 * @author Adam Huang <poisondog@gmail.com>
 */
public class PhotoFragment extends Fragment {
	private static final String PATH = "path";
	private String mPath;
	private ImageView mImage;
	private ImageFetcher mFetcher;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			mPath = savedInstanceState.getString(PATH);
		}

		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		try{
			String cachePath = getActivity().getExternalCacheDir().getPath() + "/";
			mFetcher = new ImageFetcher(getActivity(), (int)(dm.widthPixels), (int)(dm.heightPixels), cachePath);
			mFetcher.setImageCache(new ImageCache(getActivity(), cachePath));
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(PATH, mPath);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		int wrapContent = ViewGroup.LayoutParams.WRAP_CONTENT;
		int matchParent = ViewGroup.LayoutParams.MATCH_PARENT;

		FrameLayout fl = new FrameLayout(getActivity());
		fl.setLayoutParams(new FrameLayout.LayoutParams(matchParent, matchParent));

		ProgressBar progress = new ProgressBar(getActivity());
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(wrapContent, wrapContent);
		params.gravity = Gravity.CENTER;
		progress.setLayoutParams(params);

		mImage = new ImageView(getActivity());
//		mImage.setMaxZoom(2f);
//		mImage.setDrawingCacheEnabled(false);
		mFetcher.loadImage(mPath, mImage);

		fl.addView(progress);
		fl.addView(mImage);

		RelativeLayout layout = new RelativeLayout(getActivity());
		layout.addView(fl);
		return layout;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (RecyclingBitmapDrawable.class.isInstance(mImage.getDrawable())) {
			((RecyclingBitmapDrawable) mImage.getDrawable()).setIsDisplayed(false);
		}
//		mImage.setImageResource(R.drawable.file);
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
	}
	
	@Override
	public void onDetach(){
		super.onDetach();
	}
	
	public void setPath(String path) {
		mPath = path;
	}

}
