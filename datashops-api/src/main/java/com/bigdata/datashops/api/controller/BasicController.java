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
import com.bigdata.datashops.api.config.security.jwt.JwtUtil;
import com.bigdata.datashops.api.response.Result;
import com.bigdata.datashops.api.response.ResultCode;
import com.bigdata.datashops.api.response.ResultGenerator;
import com.bigdata.datashops.common.Constants;
import com.bigdata.datashops.service.DataSourceService;
import com.bigdata.datashops.service.JobDependencyService;
import com.bigdata.datashops.service.JobGraphService;
import com.bigdata.datashops.service.JobInstanceService;
import com.bigdata.datashops.service.JobRelationService;
import com.bigdata.datashops.service.JobResultService;
import com.bigdata.datashops.service.JobService;
import com.bigdata.datashops.service.MenuService;
import com.bigdata.datashops.service.PermissionService;
import com.bigdata.datashops.service.ProjectService;
import com.bigdata.datashops.service.RolePermissionService;
import com.bigdata.datashops.service.RoleService;
import com.bigdata.datashops.service.SysOperationService;
import com.bigdata.datashops.service.UserService;
import com.bigdata.datashops.service.YarnQueueService;

/**
 * Created by qinshiwei on 2018/1/30.
 */
@RestController
public class BasicController {
    private static Logger LOG = LoggerFactory.getLogger(BasicController.class);

    @Autowired
    protected ControllerAop controllerAop;

    @Autowired
    protected JwtUtil jwtUtil;

    @Autowired
    protected JobService jobService;

    @Autowired
    protected JobGraphService jobGraphService;

    @Autowired
    protected JobInstanceService jobInstanceService;

    @Autowired
    protected JobDependencyService jobDependencyService;

    @Autowired
    protected JobResultService jobResultService;

    @Autowired
    protected UserService userService;

    @Autowired
    protected PermissionService permissionService;

    @Autowired
    protected MenuService menuService;

    @Autowired
    protected RoleService roleService;

    @Autowired
    protected JobRelationService jobRelationService;

    @Autowired
    protected RolePermissionService rolePermissionService;

    @Autowired
    protected ProjectService projectService;

    @Autowired
    protected YarnQueueService yarnQueueService;

    @Autowired
    protected DataSourceService dataSourceService;

    //    @Autowired
    //    protected ZookeeperOperator zookeeperOperator;

    @Autowired
    protected SysOperationService sysOperationService;

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

    protected Result ok() {
        return ResultGenerator.genOkResult();
    }

    protected Result fail(ResultCode resType) {
        return ResultGenerator.genCustomResult(resType);
    }

    protected String getIp() {
        return controllerAop.getClientIp();
    }

    private String getToken() {
        return controllerAop.getToken();
    }

    protected Integer getUid() {
        String token = getToken();
        String data = jwtUtil.getUsername(token);
        data = data.split(Constants.SEPARATOR_USER_TOKEN_SALT)[0];
        return Integer.valueOf(data);
    }

}
