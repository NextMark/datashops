package com.bigdata.datashops.api.response;

import com.bigdata.datashops.common.utils.JSONUtils;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 统一API响应结果封装
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result {
    /**
     * 状态码
     */
    private final Integer code;
    /**
     * 消息
     */
    private final String msg;
    /**
     * 数据内容
     */
    private final Object data;

    private Result(final Builder builder) {
        this.code = builder.code;
        this.msg = builder.msg;
        this.data = builder.data;
    }

    public static class Builder {
        private final Integer code;
        private String msg;
        private Object data;

        public Builder(final Integer code) {
            this.code = code;
        }

        public Builder msg(final String msg) {
            this.msg = msg;
            return this;
        }

        public Builder data(final Object data) {
            this.data = data;
            return this;
        }

        public Result build() {
            return new Result(this);
        }
    }

    public Integer getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }

    public Object getData() {
        return this.data;
    }

    @Override
    public String toString() {
        return JSONUtils.toJsonString(this);
    }
}
