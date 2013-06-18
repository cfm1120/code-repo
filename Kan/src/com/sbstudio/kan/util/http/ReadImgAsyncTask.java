package com.sbstudio.kan.util.http;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.sbstudio.kan.R;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ReadImgAsyncTask extends AsyncTask<Object, Object, Object> {

	public static Object o = new Object();

	public static Map<ImageView, ReadImgAsyncTask> map = new ConcurrentHashMap<ImageView, ReadImgAsyncTask>();

	public HashMap<ImageView, SoftReference<Bitmap>> imgCache = new HashMap<ImageView, SoftReference<Bitmap>>();

	public static void load(ImageView imageView, String url, int width, int height) {

		ReadImgAsyncTask instance = map.get(imageView);
		if (instance == null) {
			instance = new ReadImgAsyncTask();
			instance.execute(imageView, url, width, height);
			map.put(imageView, instance);
		}
	}

	private ReadImgAsyncTask() {
	}

	@Override
	protected Object doInBackground(Object... params) {

		synchronized (o) {
			SoftReference<Bitmap> soft = null;
			Bitmap bitmap = null;
			ImageView imageView = (ImageView) params[0];
			if (imgCache.size() != 0 && imgCache.get(imageView) != null) {
				bitmap = imgCache.get(imageView).get();
				publishProgress(soft, imageView);
				return null;
			}

			String url = (String) params[1];
			Log.e(imageView+"", url);
			int width = (Integer) params[2];
			int height = (Integer) params[3];
			try {
				url = url == null ? "" : url;
				bitmap = ImageUtils.getBitmapFormOnline(url, width, height);
				soft = new SoftReference<Bitmap>(bitmap);
				imgCache.put(imageView, soft);
				publishProgress(soft, imageView);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	protected void onProgressUpdate(Object... values) {
		@SuppressWarnings("unchecked")
		SoftReference<Bitmap> soft = (SoftReference<Bitmap>) values[0];
		Bitmap bitmap = soft.get();
		ImageView imageView = (ImageView) values[1];
		if(bitmap == null){
			imageView.setImageResource(R.drawable.ic_launcher) ;
		}else{
		imageView.setImageBitmap(bitmap);
		}
		map.remove(imageView);
		super.onProgressUpdate(values);
	}

}
