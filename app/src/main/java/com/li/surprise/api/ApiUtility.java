package com.li.surprise.api;

import android.content.Context;

import com.li.surprise.common.Constant;

/**
 * Created by zaylor on 16/6/2.
 */
public class ApiUtility {

    private final static int REQUEST_CITY_LIST_CODE = 1;
    private static NetTools netTool;

    private static NetTools getNetToolsObj() {
        if (netTool == null) {
            netTool = new NetTools();
        }
        return netTool;
    }

    public static void stopRequest() {
        if (netTool == null)
    		return ;
    	netTool.stop();
    }

    private static String getUserCode(Context context){
            return "18516129709";
    }

    private static void addHeadParams(Context context){
        getNetToolsObj().setUserCode(getUserCode(context));
        getNetToolsObj().setProtocolVersion(Constant.protocolVersion);
        getNetToolsObj().setProtocolFormat(Constant.protocolFormat);
    }

    /**
     * 城市列表
     */
    public static void getCityList(String url,Context context,int expire,NetTools.OnRequest onRequest){
        if (url.length() > 0){
            addHeadParams(context);
            getNetToolsObj().setOnRequest(onRequest, REQUEST_CITY_LIST_CODE);
            getNetToolsObj().getFromUrl(REQUEST_CITY_LIST_CODE, url, expire, context);
        }
    }
}
