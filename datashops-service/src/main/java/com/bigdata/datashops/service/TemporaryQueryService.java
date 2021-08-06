package com.bigdata.datashops.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bigdata.datashops.dao.mapper.TemporaryQueryMapper;
import com.bigdata.datashops.model.pojo.job.TemporaryQuery;

@Service
public class TemporaryQueryService {
    @Autowired
    private TemporaryQueryMapper temporaryQueryMapper;

    public TemporaryQuery save(TemporaryQuery entity) {
        temporaryQueryMapper.insert(entity);
        return entity;
    }

    public TemporaryQuery findById(int id) {
        return temporaryQueryMapper.selectById(id);
    }

    public void deleteById(int id) {
        temporaryQueryMapper.deleteById(id);
    }

    public IPage<TemporaryQuery> findList(int pageNum, int pageSize, String name) {
        Page<TemporaryQuery> page = new Page(pageNum, pageSize);
        LambdaQueryWrapper<TemporaryQuery> lqw = Wrappers.lambdaQuery();
        if (StringUtils.isNotBlank(name)) {
            lqw.like(TemporaryQuery::getName, name);
        }
        lqw.orderByDesc(TemporaryQuery::getCreateTime);
        return temporaryQueryMapper.selectPage(page, lqw);
    }

}
