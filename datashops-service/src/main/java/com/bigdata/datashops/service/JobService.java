package com.bigdata.datashops.service;

import java.util.List;

import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bigdata.datashops.dao.data.domain.PageRequest;
import com.bigdata.datashops.dao.data.service.AbstractMysqlPagingAndSortingQueryService;
import com.bigdata.datashops.model.pojo.job.Job;

@Service
public class JobService extends AbstractMysqlPagingAndSortingQueryService<Job, Integer> {
    @Autowired
    private QuartzSchedulerService quartzSchedulerService;

    public Job getJob(Integer id) {
        return findById(id);
    }

    public Job getJobByMaskId(String id) {
        return findOneByQuery("maskId=" + id);
    }

    public Job getJobByProjectIdAndId(Integer projectId, Integer id) {
        return findOneByQuery("projectId=" + projectId + ";id=" + id);
    }

    public List<Job> findJobs(String filters) {
        return findByQuery(filters);
    }

    public Page<Job> getJobList(PageRequest pageRequest) {
        return pageByQuery(pageRequest);
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
        save(job);
    }

    @Transactional
    public void modifyJob(Job job) throws SchedulerException {
        save(job);
        quartzSchedulerService.addJobScheduler(job);
        quartzSchedulerService.rescheduleJob(job);
    }

}
