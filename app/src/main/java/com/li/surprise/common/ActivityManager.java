package com.li.surprise.common;

import android.app.Activity;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by zaylor on 16/6/2.
 */
public class ActivityManager {
    public List<Activity> activityList = new LinkedList<Activity>();

    private static ActivityManager instance;

    private ActivityManager() {
    }

    // 单例模式中获取唯一的MyApplication实例
    public static ActivityManager getInstance() {
        if (null == instance) {
            instance = new ActivityManager();
        }
        return instance;
    }

    public Activity getTopActivity(){
        int count = activityList.size();
        if(count>0){
            return activityList.get(count-1);
        }
        return null;
    }
    // 添加Activity到容器中
    public void addActivity(Activity activity) {
        activityList.add(activity);
    }
    /**
     * 结束指定的Activity(重载)
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            activityList.remove(activity);
            activity.finish();
            activity = null;
        }
    }
    // 遍历所有Activity并finish
    public void exit() {
        try {
            for (Activity activity : activityList) {
                activity.finish();
            }
            activityList.clear();
        }catch (Exception e){
            Log.v("atlas", e.getMessage());
        }
        GlobalApplication.getInstance().onTerminate();
        Log.v("exit", "exit application");
    }
}
