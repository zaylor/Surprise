package com.li.surprise.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.lang.reflect.Type;

/**
 * Created by zaylor on 16/6/2.
 */
public class JsonHelper {

	public static boolean validateJson(String json) {
		boolean result = false;
		try {
			JSONObject jo = new JSONObject(json);
			result = true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	public static <T> T parseJson(String jasonStr, Class<T> clsofT) {
		Gson gson = new Gson();
		try {
			return gson.fromJson(jasonStr, clsofT);
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}

	public static <T> T parseJson(String jasonStr,Type type){
		Gson gson = new Gson();
		try {
			return gson.fromJson(jasonStr, type);
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}

	public static String parseObject(Object object){
		Gson gson = new Gson();
		try {
			return gson.toJson(object);
		} catch (Exception e){
			return null;
		}
	}
	
	
	public static <T> T parseJson(String jasonStr,String nodeName,Class<T> clsOfT){
		JsonObject rootObject;
		Gson gson = new Gson();
		try {
			rootObject = gson.fromJson(jasonStr, JsonObject.class);
			if (rootObject.has(nodeName)) {
				String tmpStr = rootObject.get(nodeName).getAsString();
				return parseJson(tmpStr, clsOfT);
			}else
				return null;
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
		
	}
}
