package com.bigdata.datashops.api.controller;


import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;

import com.bigdata.datashops.api.aop.ControllerAop;
import com.bigdata.datashops.api.response.ResultCode;
import com.bigdata.datashops.api.response.Result;
import com.bigdata.datashops.api.response.ResultGenerator;
import com.bigdata.datashops.service.JobResultService;

/**
 * Created by qinshiwei on 2018/1/30.
 */
@RestController
public class BasicController {
    private static Logger LOG = LoggerFactory.getLogger(BasicController.class);

    @Autowired
    protected ControllerAop controllerAop;


    @Autowired
    protected JobResultService jobResultService;

    private static final Map<String, Boolean> HAS_FACADE_METHOD = new ConcurrentHashMap<>();
    private static final Map<String, Method> FACADE_METHOD_CACHE = new ConcurrentHashMap<>();

    private static Method getFacadeMethod(String fullRequestMethodStr) throws Exception {
        String[] requestMethodStr = fullRequestMethodStr.split("##");
        String facadeClassName = String.join("", requestMethodStr[0] + ".facade.",
                StringUtils.replace(requestMethodStr[1], "Controller", "Facade"));
        Class<?> facadeClass = Class.forName(facadeClassName);
        String methodName = requestMethodStr[2];
        String facadeMethodName =
                String.join("", "transform", methodName.substring(0, 1).toUpperCase(), methodName.substring(1));
        return facadeClass.getDeclaredMethod(facadeMethodName, Object.class);
    }

    protected Result ok(Object o) {
        String packageName = this.getClass().getPackage().getName();
        String fullRequestMethod =
                packageName + "##" + this.getClass().getSimpleName() + "##" + ControllerAop.REQUEST_METHOD.get()
                                                                                      .getName();
        // 是否有facade方法
        boolean hasFacadeMethod = HAS_FACADE_METHOD.computeIfAbsent(fullRequestMethod, k -> {
            try {
                return getFacadeMethod(k) != null;
            } catch (Exception e) {
                return false;
            }
        });

        // 如果有，获取facade方法
        if (hasFacadeMethod) {
            Method method = FACADE_METHOD_CACHE.computeIfAbsent(fullRequestMethod, k -> {
                try {
                    return getFacadeMethod(k);
                } catch (Exception e) {
                    LOG.warn("i cant find facade method, {}", fullRequestMethod);
                    return null;
                }
            });
            if (method != null) {
                try {
                    return resultJson(method.invoke(null, o));
                } catch (Exception e) {
                    LOG.debug("transform data error", e);
                }
            }
        }
        return resultJson(o);
    }

    private Result resultJson(Object result) {
        return ResultGenerator.genOkResult(result);
    }

    protected Result resultJson(ResultCode resType) {
        return ResultGenerator.genCustomResult(resType);
    }

    protected Result ok() {
        return ResultGenerator.genOkResult();
    }

    protected Object fail(ResultCode resType) {
        return ResultGenerator.genCustomResult(resType);
    }

    protected String getIp() {
        return controllerAop.getClientIp();
    }


}
