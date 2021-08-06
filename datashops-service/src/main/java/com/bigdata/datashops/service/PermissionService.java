package com.bigdata.datashops.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bigdata.datashops.dao.mapper.PermissionMapper;
import com.bigdata.datashops.model.pojo.user.Permission;

@Service
public class PermissionService {
    @Autowired
    private PermissionMapper permissionMapper;

    public List<Permission> findList(int uid) {
        LambdaQueryWrapper<Permission> lqw = Wrappers.lambdaQuery();
        lqw.eq(Permission::getUid, uid);
        return permissionMapper.selectList(lqw);
    }

    public void save(Permission entity) {
        permissionMapper.insert(entity);
    }

    public void deleteByWrapper(LambdaQueryWrapper<Permission> wrapper) {
        permissionMapper.delete(wrapper);
    }
}
