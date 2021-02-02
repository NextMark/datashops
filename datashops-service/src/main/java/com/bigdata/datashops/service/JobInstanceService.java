package com.bigdata.datashops.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.bigdata.datashops.dao.data.domain.PageRequest;
import com.bigdata.datashops.dao.data.service.AbstractMysqlPagingAndSortingQueryService;
import com.bigdata.datashops.model.pojo.job.Job;
import com.bigdata.datashops.model.pojo.job.JobInstance;

@Service
public class JobInstanceService extends AbstractMysqlPagingAndSortingQueryService<JobInstance, Integer> {
    @Autowired
    private JobService jobService;

    public List<JobInstance> findReadyJob(String filters) {
        return findByQuery(filters);
    }

    public JobInstance findJobInstance(String filter) {
        return findOneByQuery(filter);
    }

    public JobInstance saveEntity(JobInstance jobInstance) {
        return save(jobInstance);
    }

    public Page<JobInstance> getJobInstanceList(PageRequest pageRequest) {
        return pageByQuery(pageRequest);
    }

    public void fillJob(List<JobInstance> jobInstances) {
        for (JobInstance jobInstance : jobInstances) {
            Job job = jobService.getJobByMaskId(jobInstance.getMaskId());
            jobInstance.setJob(job);
        }
    }

}
