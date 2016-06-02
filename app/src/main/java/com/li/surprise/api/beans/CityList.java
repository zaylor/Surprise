package com.li.surprise.api.beans;

import java.util.List;

/**
 * Created by ganwei on 15/6/13.
 */
public class CityList {

    private Body body;

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public class Body {
        private List<GroupItemBean> data;

        public List<GroupItemBean> getData() {
            return data;
        }

        public void setData(List<GroupItemBean> data) {
            this.data = data;
        }

    }
}
