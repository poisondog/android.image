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

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import java.lang.ref.WeakReference;
import poisondog.core.Mission;

/**
 * @author Adam Huang
 */
public class MissionDrawable extends BitmapDrawable {
//	private final WeakReference<Object> mObjectReference;
//	private final WeakReference<Mission<Object>> mImageMissionReference;

	public MissionDrawable(Resources res, Bitmap bitmap, Object obj, Mission<Object> mission) {
		super(res, bitmap);
//		mObjectReference = new WeakReference<Object>(obj);
//		mImageMissionReference = new WeakReference<Mission<Object>>(mission);
	}

//	public Mission<Object> getImageMission() {
//		return mImageMissionReference.get();
//	}

//	public Object getObject() {
//		return mObjectReference.get();
//	}

}
