package com.bigdata.datashops.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bigdata.datashops.dao.mapper.RolePermissionMapper;
import com.bigdata.datashops.model.pojo.user.RolePermission;

@Service
public class RolePermissionService {
    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    public List<RolePermission> getRolePermissionByRoleIds(List<Integer> roleId) {
        LambdaQueryWrapper<RolePermission> lqw = Wrappers.lambdaQuery();
        lqw.in(RolePermission::getRoleId, roleId);
        return rolePermissionMapper.selectList(lqw);
    }

    public void deleteByWrapper(LambdaQueryWrapper<RolePermission> wrapper) {
        rolePermissionMapper.delete(wrapper);
    }

    public void save(RolePermission entity) {
        rolePermissionMapper.insert(entity);
    }
}
