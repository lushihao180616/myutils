package com.lushihao.myutils.response.vo;

import java.util.Map;

public class LSHResponse {
    /**
     * 成功/失败
     */
    private boolean result;
    /**
     * 返回数据
     */
    private Map<String, Object> bean;
    /**
     * 错误信息
     */
    private String errMsg;

    public LSHResponse(Map<String, Object> bean) {
        this.result = true;
        this.bean = bean;
        this.errMsg = null;
    }

    public LSHResponse(String errMsg) {
        this.result = false;
        this.bean = null;
        this.errMsg = errMsg;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public Map<String, Object> getBean() {
        return bean;
    }

    public void setBean(Map<String, Object> bean) {
        this.bean = bean;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
}