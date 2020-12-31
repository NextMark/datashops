package com.bigdata.datashops.api.controller.v1;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bigdata.datashops.api.config.security.jwt.JwtSetting;
import com.bigdata.datashops.api.controller.BasicController;
import com.bigdata.datashops.api.response.ResultCode;
import com.bigdata.datashops.common.Constants;
import com.bigdata.datashops.model.dto.DtoRegister;
import com.bigdata.datashops.model.pojo.user.Menu;
import com.bigdata.datashops.model.pojo.user.Permission;
import com.bigdata.datashops.model.pojo.user.User;
import com.google.common.collect.Maps;

@RestController
@RequestMapping("/v1/user")
public class UserController extends BasicController {
    @Resource
    private JwtSetting jwtSetting;

    @PostMapping("/register")
    public Object register(@RequestBody DtoRegister params) {
        User dbUser = userService.getUser("phone=" + params.getEmail());
        if (dbUser != null) {
            return fail(ResultCode.USER_REGISTERED);
        }
        dbUser = userService.getUser("email=" + params.getEmail());
        if (dbUser != null) {
            return fail(ResultCode.USER_REGISTERED);
        }
        String name = params.getName();
        String pwd = params.getPassword();
        if (StringUtils.isBlank(name) || StringUtils.isBlank(pwd)) {
            return fail(ResultCode.USER_INPUT_ILLEGAL);
        }
        User user = new User();
        user.setPhone(params.getPhone());
        user.setEmail(params.getEmail());
        user.setName(name);
        user.setPassword(pwd);
        userService.register(user);
        return ok();
    }

    @RequestMapping(value = "/login")
    public Object login(String name, String phone, String password) {
        Map<String, Object> map = Maps.newHashMap();
        String filter = null;
        if (StringUtils.isNoneBlank(name)) {
            filter = "name=" + name;
        }
        if (StringUtils.isNoneBlank(phone)) {
            filter = "phone=" + phone;
        }
        User user = userService.getUser(filter);
        if (!userService.verifyPassword(password, user.getPassword())) {
            return fail(ResultCode.USER_INPUT_ILLEGAL);
        }
        user.setLastLoginTime(new Date());
        userService.save(user);
        String token = jwtUtil.sign(
                String.format("%s%s%s%s%s", user.getId(), Constants.SEPARATOR_USER_TOKEN_SALT, user.getEmail(),
                        Constants.SEPARATOR_USER_TOKEN_SALT, user.getPhone()));
        map.put(jwtSetting.getHeader(), token);
        return ok(map);
    }

    @RequestMapping(value = "/info")
    public Object info() {
        Integer uid = getUid();
        User user = userService.getUserInfo(uid);
        return ok(user);
    }

    @RequestMapping(value = "/getMenuList")
    public Object getMenuList() {
        int uid = getUid();
        List<Permission> permissions = permissionService.getPermissionList(uid);
        List ids = permissions.stream().map(Permission::getMenuId).collect(Collectors.toList());
        String filter = "id=" + StringUtils.join(ids, Constants.SEPARATOR_COMMA);
        List<Menu> menus = menuService.getMenus(filter);
        return ok(menus);
    }
}
