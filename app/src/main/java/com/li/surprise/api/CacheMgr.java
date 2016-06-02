package com.li.surprise.api;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.util.Log;

import com.li.surprise.common.SystemUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.util.Calendar;
import java.util.Date;
/**
 * Created by zaylor on 16/6/2.
 */
public class CacheMgr {
	public static String cacheFolder = "/Cache/";
	private static int defaultExpiresInSeconds = 2 * 60;
	private static int CONNECT_TIMEOUT = 15 * 1000;
	private static int READ_TIMEOUT = 15 * 1000;
	public static String protocolVersion,protocolFormat;  //protocol版本和格式化方式

//	private static String LastErr = "";
//	private static HashMap<String, String> cacheMap = new HashMap<String, String>();
//	private static int tempFileName = 0;

	public static void ClearAllCache(Context context) {
		File cacheDir = new File(context.getFilesDir().getAbsolutePath()
				+ cacheFolder);
		if (!cacheDir.exists())
			return;

		DeleteFile(cacheDir);
	}

	public static void ClearExpiresCache(Context context) {
		File cacheDir = new File(context.getFilesDir().getAbsolutePath()
				+ cacheFolder);
		if (!cacheDir.exists())
			return;
	}

	public static Drawable getDrawableFromURL(String url, Context context) {
		Drawable drawable = null;
		try {
			InputStream is = CacheMgr.GetResource("","","",url,
					1000 * 60 * 60 * 24 * 30, context);
			if (is.available() < 1024 * 500) {
				drawable = Drawable.createFromStream(CacheMgr.GetResource("", "", "", url,
						1000 * 60 * 60 * 24 * 30, context), "src");
			}
		} catch (Exception e) {
		}
		return drawable;
	}

	public static boolean checkNetwork(Context ctx) {

		ConnectivityManager cwjManager = (ConnectivityManager) ctx
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cwjManager.getActiveNetworkInfo() != null) {
			return cwjManager.getActiveNetworkInfo().isAvailable();
		} else {
			return false;
		}
	}

	/*
	public static boolean DeleteSpecialCache(Context context, String url) {
		boolean result = false;
		try {
			String headPath = context.getFilesDir().getAbsolutePath()
					+ cacheFolder + shortUrl(url) + ".head";
			String bodyPath = context.getFilesDir().getAbsolutePath()
					+ cacheFolder + shortUrl(url) + ".body";

			bodyFile = new File(bodyPath);
			DeleteFile(bodyFile);
			headFile = new File(headPath);
			DeleteFile(headFile);
			result = true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
*/
	
	public static boolean DoExpireCache(Context context, String url) {
		File headFile, bodyFile;
		try {

			String headPath = context.getFilesDir().getAbsolutePath()
					+ cacheFolder + shortUrl(url) + ".head";
			String bodyPath = context.getFilesDir().getAbsolutePath()
					+ cacheFolder + shortUrl(url) + ".body";

			bodyFile = new File(bodyPath);
			if (bodyFile.exists()) {
				headFile = new File(headPath);
				if (headFile.exists()) {

					byte[] buffer = new byte[1024];
					FileInputStream headIs = new FileInputStream(headFile);
					headIs.read(buffer);
					String headText = new String(buffer, "UTF-8").trim();
					String[] params = headText.split(";");

					OutputStream osHead = new FileOutputStream(headFile);
					String headContent = params[0] + ";0";
					osHead.write(headContent.getBytes("UTF-8"));
					osHead.close();

				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

	public static String shortUrl(String url) {
		String result = "";
		for (int i = 0; i < url.length(); i++) {
			if ((url.charAt(i) != '/') && (url.charAt(i) != ':')
					&& (url.charAt(i) != '?') && (url.charAt(i) != '&')
					&& (url.charAt(i) != '.') && (url.charAt(i) != '='))
				result = result + url.charAt(i);
		}

//		result = MD5(url);
		return result;
	}

	public static InputStream GetResource(final String vid,final String userCode,final String access_token,final String resourceUrl,
			long expiresInSeconds, Context context) {
		File headFile, bodyFile;
		InputStream is = null;
		boolean needNewCache = false;
		if (expiresInSeconds == 0)
			expiresInSeconds = defaultExpiresInSeconds;

		String headPath = context.getFilesDir().getAbsolutePath()
				+ cacheFolder + shortUrl(resourceUrl) + ".head";
		String bodyPath = context.getFilesDir().getAbsolutePath()
				+ cacheFolder + shortUrl(resourceUrl) + ".body";

		bodyFile = new File(bodyPath);
		headFile = new File(headPath);
		try {

			if (bodyFile.exists()) {

				if (headFile.exists()) {
					byte[] buffer = new byte[1024];
					FileInputStream headIs = new FileInputStream(headFile);
					headIs.read(buffer);
					String headText = new String(buffer, "UTF-8").trim();
					String[] params = headText.split(";");
					if (params != null) {
						Date startDate = new Date(params[0]);
						Date expiresDate = AddSecond(startDate,
								Integer.parseInt(params[1]));
						if (expiresDate.after(new Date())) // δ����
							is = new FileInputStream(bodyFile);
						else {
							needNewCache = true;
							if (!checkNetwork(context)) { 
								needNewCache = false;
								is = new FileInputStream(bodyFile);
							}
						}
						headIs.close();
					}
				} else {
					needNewCache = true;
				}
			} else
				needNewCache = true;

			if (needNewCache) {
				URL url = new URL(resourceUrl);
				//Log.v("api_debug", "Url:"+resourceUrl);
				URLConnection conn = url.openConnection();
				conn.setConnectTimeout(CONNECT_TIMEOUT);
				conn.setReadTimeout(READ_TIMEOUT);

				conn.setRequestProperty("protocolVersion", protocolVersion);
				conn.setRequestProperty("protocolFormat", protocolFormat);
				conn.setRequestProperty("mcode", userCode);
				conn.setRequestProperty("vid",vid);
				conn.setRequestProperty("Authorization", "Basic " + access_token);
				conn.setRequestProperty("version", SystemUtil.getAppVersionName(context));
				Log.d("Api", "Get Url:" + url.toString() + ",mcode:" + userCode + ",vid:" + vid +
						",protocolVersion:" + protocolVersion + ",protocolFormat:" + protocolFormat);
				is = conn.getInputStream();

				byte[] indata = InputStream2Byte(is);
				is.close();
				is = null;
				if (indata.length > 1024 * 600)
					return null;
				is = new ByteArrayInputStream(indata);

				SetResource(indata, headPath, bodyPath, expiresInSeconds,
						context);

			}
		} catch (Exception ex) {
			DeleteFile(headFile);
			DeleteFile(bodyFile);
		}

		return is;
	}

	public static byte[] InputStream2Byte(InputStream is) throws IOException {

		ByteArrayOutputStream bytestream = new ByteArrayOutputStream();

		int ch;

		while ((ch = is.read()) != -1) {

			bytestream.write(ch);

		}

		byte imgdata[] = bytestream.toByteArray();

		bytestream.close();

		return imgdata;

	}

	private static void SetResource(byte[] data, String headstr,
			String bodystr, long expiresInSeconds, Context context) {
		InputStream is;
		// InputStream is = new ByteArrayInputStream(data);
		File headFile, bodyFile;

		File cacheDir = new File(context.getFilesDir().getAbsolutePath()
				+ cacheFolder);
		if (!cacheDir.exists())
			cacheDir.mkdirs();

		String headPath = headstr;
		String bodyPath = bodystr;

		// write head
		headFile = new File(headPath);
		bodyFile = new File(bodyPath);
		try {
			if (!headFile.exists())
				headFile.createNewFile();

			OutputStream osHead = new FileOutputStream(headFile);

			Date now = new Date();
			
			String headContent = now.toGMTString() + ";" + expiresInSeconds;
			osHead.write(headContent.getBytes("UTF-8"));
			osHead.close();

			if (!bodyFile.exists())
				bodyFile.createNewFile();

			OutputStream osBody = new FileOutputStream(bodyFile);

			is = new ByteArrayInputStream(data);

			byte[] buff = new byte[1024];
			int hasRead = 0;
			while ((hasRead = is.read(buff)) > 0) {
				osBody.write(buff, 0, hasRead);
			}
			// is.reset();
			// is.close();
			osBody.close();
		} catch (Exception ex) {
			DeleteFile(headFile);
			DeleteFile(bodyFile);
			ex.printStackTrace();
		}
	}

	private static Date AddSecond(Date date, int seconds) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.SECOND, seconds);
		return calendar.getTime();
	}

	private static void DeleteFile(File file) {
		try {
			if (file.exists()) {
				if (file.isFile()) {
					file.delete();
				} else if (file.isDirectory()) {
					File files[] = file.listFiles();
					for (int i = 0; i < files.length; i++) {
						DeleteFile(files[i]);
					}
				}
				file.delete();
			}
		} catch (Exception ex) {

		}
	}
	
	
	
	/**
	 * ��ȡ�����ļ���С��
	 * 
	 * @return String
	 * */
	public static String getCacheSize(Context context) {
		File cacheDirectory = new File(context.getFilesDir().getAbsolutePath()
				+ cacheFolder);
		if (cacheDirectory != null && cacheDirectory.exists()
				&& cacheDirectory.isDirectory()) {
			return formatFileSize(getDirectorySize(cacheDirectory));
		}
		return "0";
	}

	/**
	 * ��ȡ�ļ��д�С
	 * **/
	public static long getDirectorySize(File dir) {
		long size = 0;
		if (dir != null && dir.exists() && dir.isDirectory()) {
			for (File f : dir.listFiles()) {
				if (f.isDirectory()) {
					size += getDirectorySize(f);
				} else {
					size += f.length();
				}
			}
		}
		return size;
	}

	public static String getImageSize(Context context, String url) {
		String headPath = context.getFilesDir().getAbsolutePath() + cacheFolder
				+ shortUrl(url) + ".body";
		File file = new File(headPath);
		if (!file.exists()) {
			return "";
		}
		return formatFileSize(file.length());
	}

	public static String formatFileSize(long length) {
		if (length == 0)
			return "0";

		String result = null;
		int sub_string = 0;
		if (length >= 1073741824) {
			sub_string = String.valueOf((float) length / 1073741824).indexOf(
					".");
			result = ((float) length / 1073741824 + "000").substring(0,
					sub_string + 3) + "GB";
		} else if (length >= 1048576) {
			sub_string = String.valueOf((float) length / 1048576).indexOf(".");
			result = ((float) length / 1048576 + "000").substring(0,
					sub_string + 3) + "MB";
		} else if (length >= 1024) {
			sub_string = String.valueOf((float) length / 1024).indexOf(".");
			result = ((float) length / 1024 + "000").substring(0,
					sub_string + 3) + "KB";
		} else if (length < 1024) {
			result = Long.toString(length) + "B";
		}

		return result;
	}

	public static String MD5(String str) {
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}

		char[] charArray = str.toCharArray();
		byte[] byteArray = new byte[charArray.length];

		for (int i = 0; i < charArray.length; i++) {
			byteArray[i] = (byte) charArray[i];
		}
		byte[] md5Bytes = md5.digest(byteArray);

		StringBuffer hexValue = new StringBuffer();
		for (int i = 0; i < md5Bytes.length; i++) {
			int val = ((int) md5Bytes[i]) & 0xff;
			if (val < 16) {
				hexValue.append("0");
			}
			hexValue.append(Integer.toHexString(val));
		}
		return hexValue.toString();
	}
}
