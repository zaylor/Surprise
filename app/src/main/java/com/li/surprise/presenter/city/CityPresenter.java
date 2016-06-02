package com.li.surprise.presenter.city;


import android.content.Context;

import com.li.surprise.api.beans.GroupItemBean;
import com.li.surprise.common.ResultClick;
import com.li.surprise.model.city.CityModel;
import com.li.surprise.model.city.CityModelImpl;
import com.li.surprise.view.ui.city.CityViewImpl;

import java.util.List;

/**
 * Created by lizili on 16/6/2.
 */
public class CityPresenter {

    private CityModelImpl mModel;
    private CityViewImpl mView;
    private Context mContext;

    public CityPresenter(CityViewImpl curView,Context curContext){
        mView = curView;
        mContext = curContext;
        mModel = new CityModel(mContext);
    }

    public void getCity(){
        mModel.getCity(getCityClick);
    }

    //获取城市－集合－回调
    ResultClick<List<GroupItemBean>> getCityClick = new ResultClick<List<GroupItemBean>>() {
        @Override
        public void onSuccess(List<GroupItemBean> obj) {
            mView.loadData(obj);
        }

        @Override
        public void onTimeOut() {
            mView.showError("获取城市[超时]");
        }

        @Override
        public void onError(int code, String errStr) {
            mView.showError(errStr);
        }
    };
}
