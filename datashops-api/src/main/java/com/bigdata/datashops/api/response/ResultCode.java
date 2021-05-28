package com.bigdata.datashops.api.response;

/**
 * Created by qinshiwei on 2018/1/30.
 */
public enum ResultCode {
    SUCCESS(1000, "OK"), FAILURE(1001, "Params error"), SERVER_ERROR(1002, "Server error"),
    USER_REGISTERED(2000, "user registered"), USER_INPUT_ILLEGAL(2001, "input illegal");

    private Integer code;
    private String message;

    ResultCode(Integer errCode, String errMsg) {
        this.code = errCode;
        this.message = errMsg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
