package com.li.surprise.view.ui.city;

import android.widget.ListView;

import com.li.surprise.R;
import com.li.surprise.api.beans.GroupItemBean;
import com.li.surprise.presenter.city.CityPresenter;
import com.li.surprise.view.base.BaseActivity;
import com.li.surprise.widget.baseAdapter.ViewHolder;
import com.li.surprise.widget.baseAdapter.abslistview.CommonAdapter;

import java.util.List;

/**
 * Created by lizili on 16/6/2.
 */
public class CityActivity extends BaseActivity implements CityViewImpl  {

    private CityPresenter presenter = new CityPresenter(this,this);
    private ListView lv_city;
    private CommonAdapter<GroupItemBean> commonAdapter;

    @Override
    protected int setLayout() {
        return R.layout.activity_city;
    }

    @Override
    protected void initView() {
        lv_city = (ListView) findViewById(R.id.lv_city);
    }

    @Override
    protected void onResumeLoad() {
        presenter.getCity();
    }

    @Override
    public void loadData(List<GroupItemBean> data) {
        if(data != null){
            commonAdapter = new CommonAdapter<GroupItemBean>
                    (CityActivity.this,R.layout.adapter_city_select,data) {
                @Override
                public void convert(ViewHolder holder, GroupItemBean groupItemBean) {
                    holder.setText(R.id.group_name, groupItemBean.getGroupName());
                }
            };
            lv_city.setAdapter(commonAdapter);
        }
    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showError(String msg) {
        showToast(msg);
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void toActivity(boolean isToLogin) {

    }
}
