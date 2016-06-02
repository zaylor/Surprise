package com.li.surprise.common;

import android.app.Application;

/**
 * Created by zaylor on 16/6/2.
 */
public class GlobalApplication extends Application {

    private final String TAG = GlobalApplication.class.getSimpleName();
    private static GlobalApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        initHost();
    }

    private void initHost(){
        Constant.host = "http://devapi.weiwuu.net:8080/";
    }

    public GlobalApplication() {
        super();
    }

    public static GlobalApplication getInstance(){return instance;}

}
