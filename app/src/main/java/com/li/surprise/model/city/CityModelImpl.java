package com.li.surprise.model.city;

import com.li.surprise.api.beans.GroupItemBean;
import com.li.surprise.common.ResultClick;

import java.util.List;

/**
 * Created by zaylor on 16/6/2.
 */
public interface CityModelImpl {
    // get city
    void getCity(ResultClick<List<GroupItemBean>> resultClick);
}
