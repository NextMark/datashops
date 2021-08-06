package com.bigdata.datashops.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bigdata.datashops.dao.mapper.SysOperationMapper;
import com.bigdata.datashops.model.pojo.user.SysOperation;

@Service
public class SysOperationService {
    @Autowired
    private SysOperationMapper sysOperationMapper;

    public void save(SysOperation entity) {
        sysOperationMapper.insert(entity);
    }

    public void deleteById(int id) {
        sysOperationMapper.deleteById(id);
    }

    public IPage<SysOperation> findList(int pageNum, int pageSize, String name) {
        Page<SysOperation> page = new Page(pageNum, pageSize);
        LambdaQueryWrapper<SysOperation> lqw = Wrappers.lambdaQuery();
        if (StringUtils.isNotBlank(name)) {
            lqw.like(SysOperation::getPath, name);
        }
        lqw.orderByDesc(SysOperation::getCreateTime);
        return sysOperationMapper.selectPage(page, lqw);
    }
}
