package com.li.surprise.view.ui.city;

import com.li.surprise.api.beans.GroupItemBean;

import java.util.List;

/**
 * Created by lizili on 16/6/2.
 */
public interface CityViewImpl {
    // 服务评价回调
    void loadData(List<GroupItemBean> data);

    void hideLoading();

    void showError(String msg);

    void showLoading();

    void toActivity(boolean isToLogin);
}
