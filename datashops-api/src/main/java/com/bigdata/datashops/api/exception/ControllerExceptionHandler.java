package com.bigdata.datashops.api.exception;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.bigdata.datashops.api.response.Result;
import com.bigdata.datashops.api.response.ResultCode;
import com.bigdata.datashops.model.exception.DataShopsMessageException;
import com.bigdata.datashops.model.exception.ValidationException;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@RestControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler({ValidationException.class})
    public Result handlerValidationException(ValidationException exception) {
        return new Result.Builder(ResultCode.FAILURE.getCode()).msg(exception.getMessage()).build();
    }

    @ExceptionHandler({DataShopsMessageException.class})
    public Result handlerdatashopsMessageException(DataShopsMessageException exception) {
        return new Result.Builder(ResultCode.FAILURE.getCode()).msg(exception.getMessage()).build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result handlerNotValidException(MethodArgumentNotValidException exception) {
        BindingResult result = exception.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();
        return new Result.Builder(ResultCode.FAILURE.getCode()).msg(fieldErrors.get(0).getDefaultMessage()).build();
    }

    @ExceptionHandler(Exception.class)
    public Result handlerAllException(Exception exception, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.warn("path:{} throw exception", requestURI, exception);
        return new Result.Builder(ResultCode.SERVER_ERROR.getCode()).msg(exception.getMessage()).build();
    }
}
