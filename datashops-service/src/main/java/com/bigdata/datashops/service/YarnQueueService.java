package com.bigdata.datashops.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bigdata.datashops.dao.mapper.YarnQueueMapper;
import com.bigdata.datashops.model.pojo.YarnQueue;

@Service
public class YarnQueueService {
    @Autowired
    private YarnQueueMapper yarnQueueMapper;

    public void save(YarnQueue yarnQueue) {
        yarnQueueMapper.insert(yarnQueue);
    }

    public void deleteById(int id) {
        yarnQueueMapper.deleteById(id);
    }

    public IPage<YarnQueue> findList(int pageNum, int pageSize, String name) {
        Page<YarnQueue> page = new Page(pageNum, pageSize);
        LambdaQueryWrapper<YarnQueue> lqw = Wrappers.lambdaQuery();
        if (StringUtils.isNoneBlank(name)) {
            lqw.like(YarnQueue::getName, name);
        }
        lqw.orderByDesc(YarnQueue::getUpdateTime);
        return yarnQueueMapper.selectPage(page, lqw);
    }
}
