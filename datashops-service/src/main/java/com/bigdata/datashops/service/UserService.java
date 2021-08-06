package com.bigdata.datashops.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bigdata.datashops.dao.mapper.UserMapper;
import com.bigdata.datashops.model.pojo.user.Permission;
import com.bigdata.datashops.model.pojo.user.Role;
import com.bigdata.datashops.model.pojo.user.User;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private RoleService roleService;

    @Resource
    private PasswordEncoder passwordEncoder;

    public User getUserInfo(Integer uid) {
        return userMapper.selectById(uid);
    }

    public void save(User entity) {
        userMapper.insert(entity);
    }

    public User getUserByPhone(String phone) {
        LambdaQueryWrapper<User> lqw = Wrappers.lambdaQuery();
        lqw.eq(User::getPhone, phone);
        return userMapper.selectOne(lqw);
    }

    public User getUserByEmail(String email) {
        LambdaQueryWrapper<User> lqw = Wrappers.lambdaQuery();
        lqw.eq(User::getEmail, email);
        return userMapper.selectOne(lqw);
    }

    public User getUser(String name, String phone) {
        LambdaQueryWrapper<User> lqw = Wrappers.lambdaQuery();
        if (StringUtils.isNoneBlank(name)) {
            lqw.eq(User::getName, name);
        }
        if (StringUtils.isNoneBlank(phone)) {
            lqw.eq(User::getPhone, phone);
        }
        return userMapper.selectOne(lqw);
    }

    public User register(User user) {
        if (StringUtils.isNoneBlank(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword().trim()));
        }
        userMapper.insert(user);
        return user;
    }

    public boolean verifyPassword(final String rawPassword, final String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public void deleteById(int id) {
        userMapper.deleteById(id);
    }

    public IPage<User> findList(int pageNum, int pageSize) {
        Page<User> page = new Page(pageNum, pageSize);
        LambdaQueryWrapper<User> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(User::getCreateTime);
        return userMapper.selectPage(page, lqw);
    }

    public void fillRoles(List<User> users) {
        users.forEach(user -> {
            List<Permission> permissions = permissionService.findList(user.getId());
            if (permissions.size() > 0) {
                List<Integer> roleIds = permissions.stream().map(Permission::getRoleId).collect(Collectors.toList());
                List<Role> roles = roleService.findListByIds(roleIds);
                user.setRoleList(roles);
            }
        });
    }
}
