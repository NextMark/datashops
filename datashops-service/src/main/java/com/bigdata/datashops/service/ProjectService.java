package com.bigdata.datashops.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bigdata.datashops.dao.mapper.ProjectMapper;
import com.bigdata.datashops.model.pojo.Project;

@Service
public class ProjectService {
    @Autowired
    private ProjectMapper projectMapper;

    public void save(Project entity) {
        projectMapper.insert(entity);
    }

    public void deleteById(int id) {
        projectMapper.deleteById(id);
    }

    public IPage<Project> findList(int pageNum, int pageSize, String name) {
        Page<Project> page = new Page(pageNum, pageSize);
        LambdaQueryWrapper<Project> lqw = Wrappers.lambdaQuery();
        lqw.eq(Project::getName, name);
        lqw.orderByDesc(Project::getUpdateTime);
        return projectMapper.selectPage(page, lqw);
    }
}
