package com.bigdata.datashops.api.response;

import org.springframework.http.HttpStatus;

/**
 * 生成响应结果
 *
 */
public class ResultGenerator {
    private static final String DEFAULT_UNAUTHORIZED_MESSAGE = "Need authorized";
    private static final String DEFAULT_METHOD_NOT_ALLOWED_MESSAGE = "Request method incorrect";

    public static Result genOkResult() {
        return new Result
                .Builder(ResultCode.SUCCESS.getCode())
                .msg(ResultCode.SUCCESS.getMessage())
                .build();
    }

    public static Result genOkResult(final Object data) {
        return new Result
                .Builder(ResultCode.SUCCESS.getCode())
                .msg(ResultCode.SUCCESS.getMessage())
                .data(data)
                .build();
    }

    public static Result genFailedResult(final String msg) {
        return new Result
                .Builder(HttpStatus.BAD_REQUEST.value())
                .msg(msg)
                .build();
    }

    public static Result genMethodErrorResult() {
        return new Result
                .Builder(HttpStatus.METHOD_NOT_ALLOWED.value())
                .msg(DEFAULT_METHOD_NOT_ALLOWED_MESSAGE)
                .build();
    }

    public static Result genUnauthorizedResult() {
        return new Result
                .Builder(HttpStatus.UNAUTHORIZED.value())
                .msg(DEFAULT_UNAUTHORIZED_MESSAGE)
                .build();
    }

    public static Result genUnauthorizedResult(final String msg) {
        return new Result
                .Builder(HttpStatus.UNAUTHORIZED.value())
                .msg(msg)
                .build();
    }

    public static Result genInternalServerErrorResult() {
        return new Result
                .Builder(ResultCode.SERVER_ERROR.getCode())
                .msg("API internal server error")
                .build();
    }

    public static Result genCustomResult(final ResultCode msg) {
        return new Result.Builder(msg.getCode()).msg(msg.getMessage()).build();
    }

    public static Result genParamsErrorResult(final String msg) {
        return new Result
               .Builder(ResultCode.FAILURE.getCode())
               .msg(msg)
               .build();
    }
}
