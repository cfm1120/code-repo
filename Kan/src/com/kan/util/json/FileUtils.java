package com.kan.util.json;

import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;

/**
 * 文件操作的通用类
 * 
 * @author pasino
 * 
 */
public class FileUtils {

	private static final String CHAR_CODE = "UTF-8";

	public static final String SAVE_PATH = "/3sbstudio/kan";
	
	public static final int DELETEDAYS = 7 ;

	/**
	 * 得到扩展存储的绝对路径
	 * 
	 * @return
	 */
	public static String getStoragePath() {
		return Environment.getExternalStorageDirectory().getAbsolutePath();
	}

	/**
	 * 得到保存路径
	 * 
	 * @return
	 */
	public static String getSavePath() {
		return getStoragePath() + SAVE_PATH;
	}

	/**
	 * 保存字符串到文件中，如果文件不存在则创建之，无视文件是否有内容
	 * 
	 * @param file
	 *            要保存的文件
	 * @param str
	 *            内容
	 * @return
	 */
	public static boolean saveFile(File file, String str) {
		try {
			boolean result = true;

			if (!file.exists()) {
				if (!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				}
				result = file.createNewFile();
				if (!result) {
					return false;
				}
			}

			FileOutputStream fs = new FileOutputStream(file);
			byte[] buffer = str.getBytes(CHAR_CODE);
			// fs.write(buffer, 0, byteread);
			fs.write(buffer);
			fs.flush();
			fs.close();

			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 保存字符串到文件中，如果文件不存在则创建之，无视文件是否有内容
	 * 
	 * @param fileName
	 *            要保存的文件 名称
	 * @param str
	 *            内容
	 * @return
	 */
	public static boolean saveFile(String fileName, String str) {
		return saveFile(new File(fileName), str);
	}

	/**
	 * zhuijia字符串到文件中
	 * 
	 * @param fileName
	 *            要保存的文件 名称
	 * @param str
	 *            内容
	 * @return
	 */
	public static boolean appendSaveFile(String fileName, String str) {
		String content = read(fileName);
		if (content == null) {
			content = "";
		}
		return saveFile(new File(fileName), content + str);
	}

	/**
	 * 保存对象的json字符串到文件中，如果文件不存在则创建之，无视文件是否有内容
	 * 
	 * @param fileName
	 *            要保存的文件 名称
	 * @param o
	 *            内容
	 * @return
	 */
	public static boolean saveFile(String fileName, Object o) {
		return saveFile(new File(fileName), JsonUtil.format(o));
	}

	/**
	 * 复制文件
	 * 
	 * @param oldPath
	 * @param newPath
	 */
	public static boolean copy(String oldPath, String newPath) {

		File oldfile = new File(oldPath);
		return copy(oldfile, newPath);
	}

	/**
	 * 复制文件
	 * 
	 * @param oldfile
	 * @param newPath
	 * @return
	 */
	public static boolean copy(File oldfile, String newPath) {
		try {

			int byteread = 0;

			if (oldfile.exists()) {
				InputStream inStream = new FileInputStream(oldfile);
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[8196];
				while ((byteread = inStream.read(buffer)) != -1) {
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
				fs.flush();
				fs.close();
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 读取文件内容
	 * 
	 * @return
	 */
	public static String read(File file) {
		String result = null;
		try {

			int byteread = 0;

			if (file.exists()) {
				InputStream in = new FileInputStream(file);
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				byte[] buffer = new byte[8196];
				while ((byteread = in.read(buffer)) != -1) {
					out.write(buffer, 0, byteread);
				}
				result = new String(out.toByteArray(), CHAR_CODE);
				out.flush();
				in.close();
				out.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 读取文件内容
	 * 
	 * @return
	 */
	public static String read(String fileName) {
		return read(new File(fileName));
	}

	/**
	 * 删除文件
	 */
	public void deleteFiles() {
		File file = new File(SAVE_PATH);
		delete(file, Calendar.getInstance().getTime() ,DELETEDAYS);
	}

	public void delete(File file, Date time ,int day) {
		if (file == null) {

		} else if (file.isDirectory()) {
			File[] files = file.listFiles();
			if (files == null ||files.length ==0 ) {
				file.delete() ;
				return ;
			}
			for (File file2 : files) {
				delete(file2, time,day);
			}
		} else {
			long creatTime = file.lastModified();
			if ((time.getTime() - creatTime) > (1000 * 60 * 60 * 24 * day)) {
				file.delete();
			}
		}
	}
}
