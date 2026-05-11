package com.tienda.alal.model;

import java.util.Map;

public class GenericRequest {

    private Integer sp_id;
    private Map<String, Object> params;

    public Integer getSp_id() {
        return sp_id;
    }

    public void setSp_id(Integer sp_id) {
        this.sp_id = sp_id;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }
}