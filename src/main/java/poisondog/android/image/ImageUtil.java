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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.widget.ImageView;
import java.io.InputStream;
import java.io.IOException;
import poisondog.android.os.AsyncMissionTask;

/**
 * @author Adam Huang <poisondog@gmail.com>
 */
public class ImageUtil {

	public static Bitmap resize(String path, int reqWidth, int reqHeight) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);

		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
		options.inJustDecodeBounds = false;
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		options.inPurgeable = true;
		options.inInputShareable = true;
		return BitmapFactory.decodeFile(path, options);
	}

	public static Bitmap resize(InputStream input, int sampleSize) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = sampleSize;
		options.inJustDecodeBounds = false;
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		options.inPurgeable = true;
		options.inInputShareable = true;
		return BitmapFactory.decodeStream(input, null, options);
	}

	private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth) {
			final int heightRatio = Math.round((float) height / (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
			final float totalPixels = width * height;
			final float totalReqPixelsCap = reqWidth * reqHeight * 2;
			while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
				inSampleSize++;
			}
		}
		return inSampleSize;
	}

	public static int getWidth(InputStream input) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
//		BitmapFactory.decodeFile(path, options);
		BitmapFactory.decodeStream(input, null, options);
		return options.outWidth;
	}

	public static int getHeight(InputStream input) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
//		BitmapFactory.decodeFile(path, options);
		BitmapFactory.decodeStream(input, null, options);
		return options.outHeight;
	}

	public static String getType(InputStream input) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
//		BitmapFactory.decodeFile(path, options);
		BitmapFactory.decodeStream(input, null, options);
		return options.outMimeType;
	}

	public static ImageAsyncTask getImageAsyncTask(ImageView view) {
		if (view != null) {
			final Drawable drawable = view.getDrawable();
			if (drawable instanceof AsyncDrawable) {
				final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
				return asyncDrawable.getImageAsyncTask();
			}
		}
		return null;
	}

	public static AsyncMissionTask getImageTask(ImageView view) {
		if (view != null) {
			final Drawable drawable = view.getDrawable();
			if (drawable instanceof MissionDrawable) {
				final MissionDrawable asyncDrawable = (MissionDrawable) drawable;
				return asyncDrawable.getImageTask();
			}
		}
		return null;
	}

	public static Object getImageObject(ImageView view) {
		if (view != null) {
			final Drawable drawable = view.getDrawable();
			if (drawable instanceof MissionDrawable) {
				final MissionDrawable asyncDrawable = (MissionDrawable) drawable;
				return asyncDrawable.getObject();
			}
		}
		return null;
	}

	public static int resolveBitmapOrientation(String path) throws IOException {
		ExifInterface exif = new ExifInterface(path);
		return exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
	}

	public static int resolveBitmapOrientation(InputStream input) throws IOException {
		ExifInterface exif = new ExifInterface(input);
		return exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
	}

	public static Bitmap applyOrientation(Bitmap bitmap, int orientation) {
		int rotate = 0;
		switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_270:
				rotate = 270;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				rotate = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_90:
				rotate = 90;
				break;
			default:
				return bitmap;
		}
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Matrix mtx = new Matrix();
		mtx.postRotate(rotate);
		return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
	}

	/**
	 * @param path 圖片絕對路徑
	 * @return 圖片的旋轉角度
	 */
	public static int getBitmapDegree(String path) {
		int degree = 0;
		try { // 從指定路徑下讀取圖片,並獲取其EXIF資訊
			ExifInterface exifInterface = new ExifInterface(path);
			// 獲取圖片的旋轉資訊
			int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					degree = 90;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					degree = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					degree = 270;
					break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	/** * 將圖片按照某個角度進行旋轉 * * @param bm * 需要旋轉的圖片 * @param degree * 旋轉角度 * @return 旋轉後的圖片 */
	public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
		Bitmap returnBm = null; // 根據旋轉角度,生成旋轉矩陣 
		Matrix matrix = new Matrix();
		matrix.postRotate(degree);
		try { // 將原始圖片按照旋轉矩陣進行旋轉,並得到新的圖片 
			returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
		} catch (OutOfMemoryError e) {
		}
		if (returnBm == null) {
			returnBm = bm;
		}
		if (bm != returnBm) {
			bm.recycle();
		}
		return returnBm;
	}

}
