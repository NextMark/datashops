package com.bigdata.datashops.service;

import java.util.List;

import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bigdata.datashops.dao.mapper.JobMapper;
import com.bigdata.datashops.model.pojo.job.Job;

@Service
public class JobService {
    @Autowired
    private QuartzSchedulerService quartzSchedulerService;

    @Autowired
    private JobMapper jobMapper;

    public Job getJob(Integer id) {
        return jobMapper.selectById(id);
    }

    // 获取最新版本
    public Job getMaxVersionByMaskId(String maskId) {
        return jobMapper.findMaxVersionJob(maskId);
    }

    public Job getOnlineJobByMaskId(String maskId) {
        return jobMapper.findOnlineJob(maskId);
    }

    public Job getJobByMaskIdAndVersion(String maskId, int version) {
        return jobMapper.findJobByMaskIdAndVersion(maskId, version);
    }

    public void backToHistoryVersion(String maskId, int version) {
        LambdaUpdateWrapper<Job> wrapper = Wrappers.lambdaUpdate();
        wrapper.eq(Job::getMaskId, maskId).eq(Job::getStatus, 1).set(Job::getStatus, 0);
        jobMapper.update(null, wrapper);
        wrapper = Wrappers.lambdaUpdate();
        wrapper.eq(Job::getMaskId, maskId).eq(Job::getVersion, version).set(Job::getStatus, 1);
        jobMapper.update(null, wrapper);
    }

    @Transactional
    public void updateStatusOffline(String maskId, int version) {
        LambdaUpdateWrapper<Job> wrapper = Wrappers.lambdaUpdate();
        wrapper.eq(Job::getMaskId, maskId).eq(Job::getVersion, version).eq(Job::getStatus, 1).set(Job::getStatus, 0);
        jobMapper.update(null, wrapper);
    }


    public List<Job> getVersionList(String maskId) {
        LambdaQueryWrapper<Job> lqw = Wrappers.lambdaQuery();
        lqw.eq(Job::getMaskId, maskId);
        lqw.orderByDesc(Job::getStatus);
        lqw.orderByDesc(Job::getUpdateTime);
        lqw.orderByDesc(Job::getVersion);
        return jobMapper.selectList(lqw);
    }

    public IPage<Job> findByNameAndOwner(int pageNum, int pageSize, String name, String owner) {
        Page<Job> page = new Page(pageNum, pageSize);
        return jobMapper.findJobListPaging(page, name, owner);
    }

    @Transactional
    public void modifySchedulerStatus(Job job, int status) throws SchedulerException {
        job.setSchedulerStatus(status);
        if (status == 0) {
            quartzSchedulerService.pauseJob(job.getProjectId(), job.getId());
        }
        if (status == 1) {
            quartzSchedulerService.resumeJob(job.getProjectId(), job.getId());
        }
        jobMapper.updateById(job);
    }

    @Transactional
    public void modifyJob(Job job) throws SchedulerException {
        jobMapper.updateById(job);
        quartzSchedulerService.addJobScheduler(job);
        quartzSchedulerService.rescheduleJob(job);
    }

    public Job save(Job job) {
        jobMapper.insert(job);
        return job;
    }

}
