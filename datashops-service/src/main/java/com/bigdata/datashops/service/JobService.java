package com.bigdata.datashops.service;

import java.util.List;

import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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

    public Job getJobByMaskId(String maskId) {
        return jobMapper.findLatestJob(maskId);
    }

    public List<Job> getVersionList(String maskId) {
        LambdaQueryWrapper<Job> lqw = Wrappers.lambdaQuery();
        lqw.eq(Job::getMaskId, maskId);
        lqw.orderByDesc(Job::getUpdateTime);
        return jobMapper.selectList(lqw);
    }

    //    public Job getJobByProjectIdAndId(Integer projectId, Integer id) {
    //        return findOneByQuery("projectId=" + projectId + ";id=" + id);
    //    }
    //
    //    public List<Job> findJobs(String filters) {
    //        return findByQuery(filters);
    //    }
    //
    //    public Page<Job> getJobList(PageRequest pageRequest) {
    //        return pageByQuery(pageRequest);
    //    }

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
