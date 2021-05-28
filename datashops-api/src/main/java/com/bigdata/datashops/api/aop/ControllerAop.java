package com.bigdata.datashops.api.aop;

import java.lang.reflect.Method;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.bigdata.datashops.api.config.security.jwt.JwtUtil;
import com.bigdata.datashops.common.utils.JSONUtils;
import com.bigdata.datashops.model.pojo.user.SysOperation;
import com.bigdata.datashops.service.SysOperationService;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Component
@Aspect
public class ControllerAop {
    private static final Logger LOG = LoggerFactory.getLogger(ControllerAop.class);

    // private Logger requestLogger = LoggerFactory.getLogger("request");

    private static final ThreadLocal<Long> REQUEST_BEGIN_TIME_TL = new ThreadLocal<>();

    public static final ThreadLocal<Method> REQUEST_METHOD = new ThreadLocal<>();

    public static final ThreadLocal<Object> REQUEST_BODY = new ThreadLocal<>();

    public static final ThreadLocal<ProceedingJoinPoint> POINT = new ThreadLocal<>();

    private HttpServletRequest request;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private SysOperationService sysOperationService;

    @Autowired
    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    @Pointcut("execution(* com.bigdata.datashops.api.controller..*.*(..))")
    public void requestHook() {
    }

    @Before("requestHook()")
    public void doBefore(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String method = request.getMethod();
        if (method.equals(HttpMethod.POST.name()) && joinPoint.getArgs().length > 0 && joinPoint
                                                                                               .getArgs()[0] instanceof ObjectNode) {
            REQUEST_BODY.set(JSONUtils.parseObject(joinPoint.getArgs()[0].toString()));
        } else {
            ObjectNode params = JSONUtils.createObjectNode();
            request.getParameterMap().forEach(params::putPOJO);
            REQUEST_BODY.set(params);
        }
    }

    @Around(value = "com.bigdata.datashops.api.aop.ControllerAop.requestHook()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        Method method = methodSignature.getMethod();
        POINT.set(pjp);
        REQUEST_BEGIN_TIME_TL.set(System.currentTimeMillis());
        REQUEST_METHOD.set(method);
        return pjp.proceed();
    }

    @AfterReturning(returning = "ret", pointcut = "requestHook()")
    public void doAfterReturning(Object ret) {
        ProceedingJoinPoint point = POINT.get();
        Method method = REQUEST_METHOD.get();
        ObjectNode log = JSONUtils.createObjectNode();
        log.put("request", REQUEST_BODY.get().toString());
        log.put("uid", request.getHeader("uid"));
        log.put("version", request.getHeader("version"));
        log.put("network", request.getHeader("network"));
        log.put("ua", request.getHeader("User-Agent"));
        log.put("ip", getClientIp());
        log.put("action", request.getRequestURI());
        log.put("schema", request.getMethod());
        log.put("controller", point.getTarget().getClass().getSimpleName());
        log.put("method", method.getName());
        log.put("time_seconds", Instant.now().getEpochSecond());
        log.put("time", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        SysOperation sysOperation = new SysOperation();
        sysOperation.setIp(getClientIp());
        sysOperation.setOperator(request.getHeader("userName"));
        sysOperation.setMethod(request.getMethod());
        sysOperation.setPath(request.getRequestURI());
        sysOperation.setSpend((int) (System.currentTimeMillis() - REQUEST_BEGIN_TIME_TL.get()));
        sysOperationService.save(sysOperation);
        // requestLogger.info("{}", log);

        // ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        // HttpServletRequest request = attributes.getRequest();
        LOG.info("[SPEND TIME: " + (System.currentTimeMillis() - REQUEST_BEGIN_TIME_TL.get()) + " ms] "
                         + request.getRequestURI());
        POINT.remove();
        REQUEST_BEGIN_TIME_TL.remove();
        REQUEST_METHOD.remove();
        REQUEST_BODY.remove();
    }

    public String getClientIp() {
        String remoteAddr = "";
        if (request != null) {
            if (request.getHeader("x-real-ip") != null) {
                remoteAddr = request.getHeader("x-real-ip");
            } else if (request.getHeader("x-forwarded-for") != null) {
                remoteAddr = request.getHeader("x-forwarded-for");
            } else {
                remoteAddr = request.getRemoteAddr();
            }
        }
        return remoteAddr;
    }

    public String getToken() {
        return jwtUtil.getTokenFromRequest(request);
    }

}
