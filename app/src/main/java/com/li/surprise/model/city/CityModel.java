package com.li.surprise.model.city;

import android.content.Context;

import com.li.surprise.api.ApiUtility;
import com.li.surprise.api.NetTools;
import com.li.surprise.api.beans.CityList;
import com.li.surprise.api.beans.GroupItemBean;
import com.li.surprise.common.Constant;
import com.li.surprise.common.ResultClick;

import java.util.List;

/**
 * Created by zaylor on 16/6/2.
 */
public class CityModel implements CityModelImpl {

    private Context mContext;

    public CityModel(Context context){
        mContext = context;
    }

    @Override
    public void getCity(final ResultClick<List<GroupItemBean>> resultClick) {
        String url = Constant.host + "city/groupingSort";
        ApiUtility.getCityList(url, mContext, 1, new NetTools.OnRequest<CityList>() {
            @Override
            public Class<?> getT() {
                return CityList.class;
            }

            @Override
            public void onSuccess(CityList obj) {
                if (obj != null && obj.getBody().getData() != null && obj.getBody().getData().size() > 0) {
                    resultClick.onSuccess(obj.getBody().getData());
                } else
                    resultClick.onSuccess(null);
            }

            @Override
            public void onTimeOut() {
                resultClick.onTimeOut();
            }

            @Override
            public void onError(int code, String errStr) {
                resultClick.onError(code, errStr);
            }

            @Override
            public void onLog(String arg) {
            }
        });
    }
}
