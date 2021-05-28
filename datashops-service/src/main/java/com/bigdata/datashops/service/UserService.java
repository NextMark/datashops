package com.bigdata.datashops.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bigdata.datashops.common.Constants;
import com.bigdata.datashops.dao.data.domain.PageRequest;
import com.bigdata.datashops.dao.data.service.AbstractMysqlPagingAndSortingQueryService;
import com.bigdata.datashops.model.pojo.user.Permission;
import com.bigdata.datashops.model.pojo.user.Role;
import com.bigdata.datashops.model.pojo.user.User;

@Service
public class UserService extends AbstractMysqlPagingAndSortingQueryService<User, Integer> {
    @Autowired
    private PermissionService permissionService;

    @Autowired
    private RoleService roleService;

    @Resource
    private PasswordEncoder passwordEncoder;

    public User getUserInfo(Integer uid) {
        return findById(uid);
    }

    public User getUser(String filter) {
        return findOneByQuery(filter);
    }

    public User register(User user) {
        if (StringUtils.isNoneBlank(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword().trim()));
        }
        return save(user);
    }

    public boolean verifyPassword(final String rawPassword, final String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public Page<User> getUserList(PageRequest pageRequest) {
        return pageByQuery(pageRequest);
    }

    public void fillRoles(List<User> users) {
        users.forEach(user -> {
            List<Permission> permissions = permissionService.getPermissionList(user.getId());
            if (permissions.size() > 0) {
                List roleIds = permissions.stream().map(Permission::getRoleId).collect(Collectors.toList());
                String filter = "id=" + StringUtils.join(roleIds, Constants.SEPARATOR_COMMA);
                List<Role> roles = roleService.findByQuery(filter);
                user.setRoleList(roles);
            }
        });
    }
}
