package com.bigdata.datashops.api.controller.v1;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bigdata.datashops.api.common.Pagination;
import com.bigdata.datashops.api.controller.BasicController;
import com.bigdata.datashops.api.response.ResultCode;
import com.bigdata.datashops.common.Constants;
import com.bigdata.datashops.model.dto.DtoLogin;
import com.bigdata.datashops.model.dto.DtoPageQuery;
import com.bigdata.datashops.model.dto.DtoRegister;
import com.bigdata.datashops.model.pojo.user.Permission;
import com.bigdata.datashops.model.pojo.user.User;
import com.google.common.collect.Maps;

@RestController
@RequestMapping("/v1/user")
public class UserController extends BasicController {

    @PostMapping("/register")
    public Object register(@RequestBody DtoRegister params) {
        User dbUser = userService.getUserByPhone(params.getPhone());
        if (dbUser != null) {
            return fail(ResultCode.USER_REGISTERED);
        }
        dbUser = userService.getUserByEmail(params.getEmail());
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
        User user = userService.getUser(login.getName(), login.getPhone());
        if (!userService.verifyPassword(login.getPassword(), user.getPassword())) {
            return fail(ResultCode.USER_INPUT_ILLEGAL);
        }
        user.setLastLoginTime(new Date());
        userService.updateById(user);
        String token = jwtUtil.sign(
                String.format("%s%s%s%s%s", user.getId(), Constants.SEPARATOR_USER_TOKEN_SALT, user.getEmail(),
                        Constants.SEPARATOR_USER_TOKEN_SALT, user.getPhone()));
        map.put("user", user);
        map.put(Constants.JWT_HEADER, token);
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
        IPage<User> res = userService.findList(query.getPageNum(), query.getPageSize());
        List<User> userList = res.getRecords();
        userService.fillRoles(userList);
        Pagination pagination = new Pagination(res);
        return ok(pagination);
    }

}
