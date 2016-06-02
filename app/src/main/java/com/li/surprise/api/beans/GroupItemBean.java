package com.li.surprise.api.beans;

import java.util.List;

/**
 * Created by lizili on 16/6/2.
 */
public class GroupItemBean {
    private String groupName;
    private List<CityBean> citys;

    public List<CityBean> getCitys() {
        return citys;
    }

    public void setCitys(List<CityBean> citys) {
        this.citys = citys;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
