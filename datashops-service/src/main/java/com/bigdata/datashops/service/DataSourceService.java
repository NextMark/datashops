package com.bigdata.datashops.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bigdata.datashops.dao.mapper.DataSourceMapper;
import com.bigdata.datashops.model.pojo.DataSource;

@Service
public class DataSourceService {
    @Autowired
    private DataSourceMapper dataSourceMapper;

    public void save(DataSource entity) {
        dataSourceMapper.insert(entity);
    }

    public void deleteById(int id) {
        dataSourceMapper.deleteById(id);
    }

    public IPage<DataSource> findList(int pageNum, int pageSize, String name) {
        Page<DataSource> page = new Page(pageNum, pageSize);
        LambdaQueryWrapper<DataSource> lqw = Wrappers.lambdaQuery();
        if (StringUtils.isNoneBlank(name)) {
            lqw.like(DataSource::getName, name);
        }
        lqw.orderByDesc(DataSource::getUpdateTime);
        return dataSourceMapper.selectPage(page, lqw);
    }
}
