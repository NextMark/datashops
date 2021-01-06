package com.bigdata.datashops.api.controller.v1;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bigdata.datashops.api.common.Pagination;
import com.bigdata.datashops.api.config.security.jwt.JwtSetting;
import com.bigdata.datashops.api.controller.BasicController;
import com.bigdata.datashops.api.response.ResultCode;
import com.bigdata.datashops.common.Constants;
import com.bigdata.datashops.dao.data.domain.PageRequest;
import com.bigdata.datashops.model.dto.DtoLogin;
import com.bigdata.datashops.model.dto.DtoPageQuery;
import com.bigdata.datashops.model.dto.DtoRegister;
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
        user = userService.register(user);

        String[] roleIds = params.getRoleIds();
        for (String id : roleIds) {
            Permission permission = new Permission();
            permission.setRoleId(Integer.valueOf(id));
            permission.setUid(user.getId());
            permissionService.save(permission);
        }
        return ok();
    }

    @PostMapping(value = "/login")
    public Object login(@RequestBody DtoLogin login) {
        Map<String, Object> map = Maps.newHashMap();
        String filter = null;
        if (StringUtils.isNoneBlank(login.getName())) {
            filter = "name=" + login.getName();
        }
        if (StringUtils.isNoneBlank(login.getPhone())) {
            filter = "phone=" + login.getPhone();
        }
        User user = userService.getUser(filter);
        if (!userService.verifyPassword(login.getPassword(), user.getPassword())) {
            return fail(ResultCode.USER_INPUT_ILLEGAL);
        }
        user.setLastLoginTime(new Date());
        userService.save(user);
        String token = jwtUtil.sign(
                String.format("%s%s%s%s%s", user.getId(), Constants.SEPARATOR_USER_TOKEN_SALT, user.getEmail(),
                        Constants.SEPARATOR_USER_TOKEN_SALT, user.getPhone()));
        map.put("user", user);
        map.put(jwtSetting.getHeader(), token);
        return ok(map);
    }

    @RequestMapping(value = "/info")
    public Object info() {
        Integer uid = getUid();
        User user = userService.getUserInfo(uid);
        return ok(user);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public Object delete(@RequestBody Map<String, Integer> id) {
        userService.deleteById(id.get("id"));
        return ok();
    }

    @RequestMapping(value = "/getUserList")
    public Object getUserList(@RequestBody DtoPageQuery query) {
        PageRequest pageable =
                new PageRequest(query.getPageNum() - 1, query.getPageSize(), "", Sort.Direction.ASC, "createTime");
        Page<User> user = userService.getUserList(pageable);
        List<User> userList = user.getContent();
        userService.fillRoles(userList);
        Pagination pagination = new Pagination(user);
        return ok(pagination);
    }

}
