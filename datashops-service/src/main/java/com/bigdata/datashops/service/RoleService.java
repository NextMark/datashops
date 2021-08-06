package com.bigdata.datashops.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bigdata.datashops.dao.mapper.RoleMapper;
import com.bigdata.datashops.model.pojo.user.Role;

@Service
public class RoleService {
    @Autowired
    private RoleMapper roleMapper;

    public void deleteById(int id) {
        roleMapper.deleteById(id);
    }

    public void save(Role entity) {
        roleMapper.insert(entity);
    }

    public Role getRole(Integer id) {
        return roleMapper.selectById(id);
    }

    public IPage<Role> findList(int pageNum, int pageSize) {
        Page<Role> page = new Page(pageNum, pageSize);
        LambdaQueryWrapper<Role> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(Role::getCreateTime);
        return roleMapper.selectPage(page, lqw);
    }

    public List<Role> findListByIds(List<Integer> ids) {
        LambdaQueryWrapper<Role> lqw = Wrappers.lambdaQuery();
        lqw.in(Role::getId, ids);
        return roleMapper.selectList(lqw);
    }
}
