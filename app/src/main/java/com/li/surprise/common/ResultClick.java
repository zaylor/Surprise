package com.li.surprise.common;

/**
 * Created by zaylor on 16/6/2.
 */
public interface ResultClick<T> {
    void onSuccess(T obj);

    void onTimeOut();

    void onError(int code, String errStr);
}
