package com.kan.util.http;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorMatrix;
import android.util.Log;


public class BitmapUtils {

	/**
	 * 从网络上下载图片，并转换指定的长宽，
	 * @param url 网络地址
	 * @param width 宽度
	 * @param height 高度
	 * @return
	 * @throws IOException
	 */
	public static Bitmap getBitmapFormOnline(String url, int width, int height) throws IOException {
		
		String urlPath = getSavePath(url);
		Bitmap newBitmap = getBitmapFromLocal(urlPath,width,height);
		if(newBitmap != null)
		{
			return newBitmap;
		}
		
		File file = downloadBitmap(url);
		String path = file.getAbsolutePath();
		newBitmap = getBitmapFromLocal(path,width,height);
		
		return newBitmap;
	}

	/**
	 * 从网络上下载图片，并转换指定的长宽200*200，
	 * @param url 网络地址
	 * @return
	 * @throws IOException
	 */
	public static Bitmap getBitmapFormOnline200_200(String url) throws IOException {
		return getBitmapFormOnline(url,200,200);
	}
	
	/**
	 * 从网络上下载图片，并转换指定的长宽1020*679
	 * @param url 网络地址
	 * @return
	 * @throws IOException
	 */
	public static Bitmap getBitmapFormOnline1020_679(String url) throws IOException {
		return getBitmapFormOnline(url,1020,679);
	}
	
	/**
	 * 以200，200解析图片
	 * @param url
	 * @return
	 */
	public static Bitmap getBitmapFromLocal200_200(String url)
	{
		return getBitmapFromLocal(url,200,200);
	}
	
	/**
	 * 以1020——679解析图片
	 * @param url
	 * @return
	 */
	public static Bitmap getBitmapFromLocal1020_679(String url)
	{
		return getBitmapFromLocal(url,1020,679);
	}
	
	/**
	 * 从本地读取图片，不能读取返回null
	 * 
	 * @param url
	 *            图片的存储地址
	 *
	 * @param scaleWidth 要缩放的图片宽度
	 * @param scaleHeight 要缩放的图片高度
	 * @return
	 */
	public static Bitmap getBitmapFromLocal(String url, int scaleWidth , int scaleHeight) {

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		
		if(!new File(url).exists())
		{
			return null;
		}
		
		//获取原始图片
		Bitmap bitmap = BitmapFactory.decodeFile(url, options);
		
		if(bitmap != null)
		{
			bitmap.recycle();
		}
		
		
		options.inJustDecodeBounds = false;
		// 计算缩放比
		int be = (int) (options.outHeight / (float) 200);
		if (be <= 0)
			be = 1;
		options.inSampleSize = be;
		// 重新读入图片，注意这次要把options.inJustDecodeBounds 设为 false哦
		bitmap = BitmapFactory.decodeFile(url, options);

		if(bitmap == null)
		{
			return null;
		}
		
		// 获取这个图片的宽和高
		float width = bitmap.getWidth();
		float height = bitmap.getHeight();
		float scaleX = width / scaleWidth ;//1020;
		float scaleY = height / scaleHeight;// 679;
		Bitmap newBitmap = null;
		if (bitmap != null) {
			if (scaleX > scaleY) {
				float newHeight = height / scaleX;
				newBitmap = Bitmap.createScaledBitmap(bitmap, scaleWidth, (int) newHeight, true);
			} else {
				float newWidth = width / scaleY;
				newBitmap = Bitmap.createScaledBitmap(bitmap, (int) newWidth, scaleHeight, true);
			}

		}
		/*
		 * if(bitmap==null){ Log.e("get local bitmap", "bitmap is null") ;
		 * return null ; } Bitmap newBitmap =Bitmap.createScaledBitmap(bitmap,
		 * 1020, 679, true); bitmap.recycle() ;
		 */
		return newBitmap;
	}
	
	
	

	/**
	 * 读取图片转换长宽1024，768
	 * 
	 * @param url
	 * @return
	 */
	public static Bitmap getFullScreenBitmapFromLocal(String url) {

		Bitmap bitmap = null;
		bitmap = BitmapFactory.decodeFile(url);
		Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, 1024, 768, true);
		return newBitmap;
	}

	/**
	 * 通过colorMatrix设置亮度
	 * 
	 * @param cm
	 *            colorMatrix
	 * @param translate
	 *            亮度值
	 */
	public static void adjustBrightness(ColorMatrix cm, float translate) {

		// Log.v("debug", "(OPERATE_BRIGHTNESS)translate: " + translate);

		cm.set(new float[] { 1, 0, 0, 0, translate, 0, 1, 0, 0, translate, 0, 0, 1, 0, translate, 0, 0, 0, 1, 0 });
	}

	/**
	 * 通过colorMatrix设置对比度
	 * 
	 * @param cm
	 *            colorMatrix
	 * @param val
	 *            seekbar的值，用来设置对比度
	 */
	public static void adjustContrast(ColorMatrix cm, int val) {

		float contrast = val / 10.0f;

		// Log.v("debug", "contrast:" + contrast);

		cm.set(new float[] { contrast, 0, 0, 0, 128 * (1 - contrast), 0, contrast, 0, 0, 128 * (1 - contrast), 0, 0,
				contrast, 0, 128 * (1 - contrast), 0, 0, 0, 1, 0 });
	}

	/**
	 * 通过colorMatrix设置灰度
	 * 
	 * @param cm
	 *            colorMatrix
	 * @param val
	 *            用来设置灰度的值
	 */
	public static void adjustHue(ColorMatrix cm, int val) {

		float hue = val / 100.0f;
		// Log.v("debug", "hue:" + hue);

		cm.set(new float[] { 0.3086f * (1 - hue) + hue, 0.6094f * (1 - hue), 0.0820f * (1 - hue), 0, 0,
				0.3086f * (1 - hue), 0.6094f * (1 - hue) + hue, 0.0820f * (1 - hue), 0, 0, 0.3086f * (1 - hue),
				0.6094f * (1 - hue), 0.0820f * (1 - hue) + hue, 0, 0, 0, 0, 0, 1, 0 });
	}

	/**
	 * 下载文件存放本地文件夹
	 * @param urlString 网络地址
	 * @return
	 */
	public static File downloadBitmap(String url)
	{
		String fileName = getSavePath(url);
		return downloadBitmap(url, fileName);
	}
	
	/**
	 * 切换病人时删除缓存图片
	 */
	public static void deletePictures(){
		try{
		File file = new File("/sdcard/food/") ;
		File[] files = file.listFiles() ;
		for(int i =0 ;i<files.length ;i++){
			files[i].delete() ;
		}
		}catch (Exception e) {
			e.printStackTrace() ;
		}
	}
	
	/**
	 * 将网络地址转换本地文件地址存储位置
	 * @param url
	 * @return
	 */
	public static String getSavePath(String url)
	{
		return "/sdcard/food/" + URLEncoder.encode(url);
	}
	
	/**
	 * 下载文件存放本地文件夹
	 * @param urlString 网络地址
	 * @param fileName 为存放的完整文件地址
	 * @return
	 */
	public static File downloadBitmap(String urlString, String fileName) {

		File cacheFile = new File(fileName);
		if (!cacheFile.exists()) {
			try {
				File file = new File(cacheFile.getParent());
				file.mkdirs();
				cacheFile.createNewFile();
			} catch (IOException e) {
				// 
				e.printStackTrace();
			}
		}

		HttpURLConnection urlConnection = null;
		BufferedOutputStream out = null;
		InputStream in = null;

		try {
			Log.e("network address download...", urlString);
			URL url = new URL(urlString);
			urlConnection = (HttpURLConnection) url.openConnection();
			in = new BufferedInputStream(urlConnection.getInputStream(), 10 * 1024 * 1024);
			out = new BufferedOutputStream(new FileOutputStream(cacheFile), 10 * 1024 * 1024);
			int b;
			while ((b = in.read()) != -1) {
				out.write(b);
			}

			return cacheFile;

		} catch (final IOException e) {
			e.printStackTrace();
			Log.e("network address download error", "Error in downloadBitmap - " + e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception e2) {
				}
			}
			if (urlConnection != null) {
				urlConnection.disconnect();
			}
			if (out != null) {
				try {
					out.close();
				} catch (final IOException e) {
					Log.e("network address download error", "Error in downloadBitmap - " + e);
				}
			}
		}

		return null;
	}
}
