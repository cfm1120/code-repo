/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.sbstudio.kan.util.http;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.view.Display;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;

/**
 * Just displays {@link Bitmap} in {@link ImageView}
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.5.6
 */
public class MyBitmapDisplayer implements BitmapDisplayer {
	
	private Activity activity;
	
	public MyBitmapDisplayer(Activity activity) {
		super();
		this.activity=activity;
	}

	@Override
	public Bitmap display(Bitmap bitmap, ImageView imageView, LoadedFrom loadedFrom) {
		Display display=activity.getWindowManager().getDefaultDisplay();
		int screenWidth=display.getWidth();
		
		int height=bitmap.getHeight();
		int width=bitmap.getWidth();
		
		 int newWidth = screenWidth;
	     int newHeight = screenWidth*height/width; // 根据屏幕的宽度，计算按比较缩放后的高度
		
		
		  // calculate the scale
	     float scaleWidth = ((float) newWidth) / width;
	     float scaleHeight = ((float) newHeight) / height;

	     // create a matrix for the manipulation
	     Matrix matrix = new Matrix();
	     // resize the Bitmap
	     matrix.postScale(scaleWidth, scaleHeight);
	     // if you want to rotate the Bitmap
	     // matrix.postRotate(45);
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
		
		imageView.setImageBitmap(resizedBitmap);
		
		
		return bitmap;
	}
}