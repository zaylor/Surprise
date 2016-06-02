package com.li.surprise.api;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.li.surprise.R;
import com.li.surprise.api.beans.BaseBean;
import com.li.surprise.common.SystemUtil;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
/**
 * Created by zaylor on 16/6/2.
 */
public class NetTools {

	private final int MSG_SUCCESS = 0;
	private final int MSG_ERROR = 1;
	private final int MSG_TIME_OUT = 2;
	private final int MSG_LOG = 3;
	private final int ERROR_PARSER_JSON = 1000;
	private int TIME_OUT_INTERVAL = 1000 * 100;

	private HashMap<Integer, OnRequest> onRequestMap;
    private String userCode,access_token,vid=android.os.Build.SERIAL,version;
    private String protocolVersion,protocolFormat;  //protocol版本和格式化方式

    public void setAccessToken(String access_token) {
        this.access_token = access_token;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }
    public void setProtocolVersion(String protocolVersion){
        this.protocolVersion = protocolVersion;
    }
    public void setProtocolFormat(String protocolFormat){
        this.protocolFormat = protocolFormat;
    }

    public NetTools() {
		onRequestMap = new HashMap<Integer, OnRequest>();
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			int requestcode = (Integer) msg.arg2;
			switch (msg.what) {
				case MSG_SUCCESS:
					OnRequest onRequest_success = onRequestMap.get(requestcode);
					String xml = (String) msg.obj;
					if (onRequest_success != null) {
						onRequest_success.onSuccess(JsonHelper.parseJson(xml,
								onRequest_success.getT()));
					}
					onRequestMap.remove(requestcode);
					break;
				case MSG_ERROR:
					OnRequest onRequest_error = onRequestMap.get(requestcode);
					if (onRequest_error != null) {
						onRequest_error.onError(msg.arg1, (String) msg.obj);
					}
					onRequestMap.remove(requestcode);
					break;
				case MSG_TIME_OUT:
					OnRequest onRequest_timeout = onRequestMap.get(requestcode);
					if (onRequest_timeout != null) {
						onRequest_timeout.onTimeOut();
					}
					onRequestMap.remove(requestcode);
					break;
				case MSG_LOG:
					OnRequest onRequest_log = onRequestMap.get(requestcode);
					if (onRequest_log != null) {
						onRequest_log.onLog((String) msg.obj);
					}
					break;
				default:
					break;
			}

		}
	};

	/**
	 * 通用错误处理
	 *
	 * @param index
	 * @param json
	 */
	private void handleError(int index, String json) {

		handler.obtainMessage(MSG_LOG, 0, index, json).sendToTarget();

		if (JsonHelper.validateJson(json)) {

            BaseBean result = JsonHelper.parseJson(json, BaseBean.class);
            if (result != null && result.getHead() != null && result.getHead().getCode() == 1){
                handler.obtainMessage(MSG_SUCCESS, 0, index, json)
								.sendToTarget();
            }else{
                if (result != null && result.getHead() != null)
                    handler.obtainMessage(
									MSG_ERROR,
                                    result.getHead().getCode(),
									index, result.getHead().getMessage())
									.sendToTarget();
                else
                    handler.obtainMessage(MSG_ERROR, ERROR_PARSER_JSON, index,
									"数据解析错误，请检查网络连接再试").sendToTarget();
            }

		} else {
			handler.obtainMessage(MSG_ERROR, ERROR_PARSER_JSON, index, "数据解析错误，请检查网络连接再试")
					.sendToTarget();
		}
	}

	public void getFromUrl(final int requestcode, final String url,
						   final int expireSecond, final Context context) {

		handler.sendMessageDelayed(
				handler.obtainMessage(MSG_TIME_OUT, 0, requestcode),
				TIME_OUT_INTERVAL);

		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String json = NetTools.this.readStringFromurlByByte(url,
						expireSecond, context);
				if (json == null) {
					handler.obtainMessage(
							MSG_ERROR,
							0,
							requestcode,
							context.getResources().getString(
									R.string.toast_net_error_info))
							.sendToTarget();
				} else {
					handleError(requestcode, json);
				}
			}

		}).start();
	}



    public void putJson(final int requestCode,final String server,
                        final JSONObject param,final Context context){
        handler.sendMessageDelayed(
                handler.obtainMessage(MSG_TIME_OUT, 0, requestCode),
                TIME_OUT_INTERVAL);
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    URL url = new URL(server);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    conn.setConnectTimeout(1000*5);
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestMethod("PUT");
                    conn.setRequestProperty("Content-Type", "application/json");

                    conn.setRequestProperty("protocolVersion", protocolVersion);
                    conn.setRequestProperty("protocolFormat", protocolFormat);
                    conn.setRequestProperty("mcode", userCode);
                    conn.setRequestProperty("vid",vid);
                    conn.setRequestProperty("Authorization","Basic "+access_token);
                    conn.setRequestProperty("version", SystemUtil.getAppVersionName(context));
                    Log.d("Api", "Put Url:" + url.toString() + ",mcode:" + userCode + ",vid:" + vid +
                            ",protocolVersion:" + protocolVersion + ",protocolFormat:" + protocolFormat);
                    OutputStream os = conn.getOutputStream();
                    String content = String.valueOf(param);
                    os.write(content.getBytes());
                    os.close();
                    int code = conn.getResponseCode();
                    if (code == 200){
                        InputStream is = conn.getInputStream();
                        String json = new String(Is2Bytes(is));
                        handleError(requestCode, json);
//                        handler.obtainMessage(MSG_SUCCESS, 0, requestCode,
//                                json).sendToTarget();
                    }else{
                        handler.obtainMessage(
                                MSG_ERROR,
                                0,
                                requestCode,
                                context.getResources().getString(
                                        R.string.toast_net_error_info))
                                .sendToTarget();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    handler.obtainMessage(
                            MSG_ERROR,
                            0,
                            requestCode,
                            context.getResources().getString(
                                    R.string.toast_net_error_info))
                            .sendToTarget();
                }


            }
        }).start();
    }

    public void postJson(final int requestCode,final String server,
                         final JSONObject param,final Context context){
        handler.sendMessageDelayed(
                handler.obtainMessage(MSG_TIME_OUT, 0, requestCode),
                TIME_OUT_INTERVAL);
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    URL url = new URL(server);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    conn.setConnectTimeout(1000*5);
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");

                    conn.setRequestProperty("protocolVersion", protocolVersion);
                    conn.setRequestProperty("protocolFormat", protocolFormat);
                    conn.setRequestProperty("mcode", userCode);
                    conn.setRequestProperty("vid",vid);
                    conn.setRequestProperty("Authorization","Basic "+access_token);
                    conn.setRequestProperty("version", SystemUtil.getAppVersionName(context));
                    Log.d("Api", "Post Url:" + url.toString() + ",mcode:" + userCode + ",vid:" + vid +
                            ",protocolVersion:" + protocolVersion + ",protocolFormat:" + protocolFormat);
                    OutputStream os = conn.getOutputStream();
                    String content = String.valueOf(param);
                    os.write(content.getBytes());
                    os.close();
                    int code = conn.getResponseCode();
                    if (code == 200){
                        InputStream is = conn.getInputStream();
                        String json = new String(Is2Bytes(is));
                        handleError(requestCode, json);
//                        handler.obtainMessage(MSG_SUCCESS, 0, requestCode,
//                                json).sendToTarget();
                    }else{
                        handler.obtainMessage(
                                MSG_ERROR,
                                0,
                                requestCode,
                                context.getResources().getString(
                                        R.string.toast_net_error_info))
                                .sendToTarget();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    handler.obtainMessage(
                            MSG_ERROR,
                            0,
                            requestCode,
                            context.getResources().getString(
                                    R.string.toast_net_error_info))
                            .sendToTarget();
                }


            }
        }).start();
    }


    public void postFile(final int requestCode,final String server,
                          final File file,final Context context){
        handler.sendMessageDelayed(
                handler.obtainMessage(MSG_TIME_OUT, 0, requestCode),
                TIME_OUT_INTERVAL);
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    URL url = new URL(server);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    conn.setConnectTimeout(1000*5);
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestMethod("POST");

                    conn.setRequestProperty("protocolVersion", protocolVersion);
                    conn.setRequestProperty("protocolFormat", protocolFormat);
                    conn.setRequestProperty("mcode", userCode);
                    conn.setRequestProperty("vid",vid);
                    conn.setRequestProperty("Authorization","Basic "+access_token);
                    conn.setRequestProperty("version",SystemUtil.getAppVersionName(context));
                    Log.d("Api", "Post file Url:" + url.toString() + ",mcode:" + userCode + ",vid:" + vid +
                            ",protocolVersion:" + protocolVersion + ",protocolFormat:" + protocolFormat);
                    conn.setRequestProperty("Content-Type","application/octet-stream");
                    OutputStream os = conn.getOutputStream();

                    InputStream is = new FileInputStream(file);
                    byte[] buffer = new byte[1024];

                    int len = 0;

                    while ((len = is.read(buffer)) != -1) {


                        os.write(buffer, 0, len);
                        os.flush();

                    }

                    is.close();

                    os.close();
                    int code = conn.getResponseCode();
                    if (code == 200){
                        InputStream is2 = conn.getInputStream();
                        String json = new String(Is2Bytes(is2));
                        System.out.println(json);
                        handleError(requestCode, json);
                    }else{
                        handler.obtainMessage(
                                MSG_ERROR,
                                0,
                                requestCode,
                                context.getResources().getString(
                                        R.string.toast_net_error_info))
                                .sendToTarget();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    handler.obtainMessage(
                            MSG_ERROR,
                            0,
                            requestCode,
                            context.getResources().getString(
                                    R.string.toast_net_error_info))
                            .sendToTarget();
                }


            }
        }).start();
    }



	public void setOnRequest(OnRequest onRequest, int requestcode) {
		onRequestMap.put(requestcode, onRequest);
	}

	public String readStringFromurlByByte(String url, int expireSecond,
										   Context context) {
        CacheMgr.protocolFormat = protocolFormat;
        CacheMgr.protocolVersion = protocolVersion;
		InputStream stream = CacheMgr.GetResource(vid,userCode,access_token, url.replaceAll(" ", "%20"),
				expireSecond, context);
		if (stream != null) {

			StringBuffer sb = new StringBuffer("");
			try {
				byte[] indata = CacheMgr.InputStream2Byte(stream);
				stream.close();
				sb.append(new String(indata, 0, indata.length, "UTF-8"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return sb.toString();

		} else {
			return null;
		}

	}

    public static byte[] Is2Bytes(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int ch;
        while ((ch = is.read()) != -1){
            baos.write(ch);
        }

        byte[] data = baos.toByteArray();
        baos.close();
        return data;
    }

	public void stop() {
		handler.removeCallbacksAndMessages(null);
		onRequestMap.clear();
	}

	public interface OnRequest<T> {

		Class<?> getT();

		void onSuccess(T obj);

        void onTimeOut();

		void onError(int code, String errStr);

		void onLog(String arg);
	}

    public static NetworkInfo getNetworkInfo(Context content){
        ConnectivityManager connectMgr = (ConnectivityManager) content.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectMgr.getActiveNetworkInfo();
    }
}
