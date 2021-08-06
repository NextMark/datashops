package com.bigdata.datashops.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bigdata.datashops.dao.mapper.ResourceFileMapper;
import com.bigdata.datashops.model.pojo.hadoop.ResourceFile;

@Service
public class ResourceFileService {
    @Autowired
    private ResourceFileMapper resourceFileMapper;

    public void save(ResourceFile entity) {
        resourceFileMapper.insert(entity);
    }

    public void deleteById(int id) {
        resourceFileMapper.deleteById(id);
    }

    public IPage<ResourceFile> findList(int pageNum, int pageSize, String name) {
        Page<ResourceFile> page = new Page(pageNum, pageSize);
        LambdaQueryWrapper<ResourceFile> lqw = Wrappers.lambdaQuery();
        lqw.ne(ResourceFile::getType, 0);
        lqw.ne(ResourceFile::getType, 1);
        if (StringUtils.isNotBlank(name)) {
            lqw.eq(ResourceFile::getName, name);
        }
        lqw.orderByDesc(ResourceFile::getCreateTime);
        return resourceFileMapper.selectPage(page, lqw);
    }
}
