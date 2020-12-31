package com.bigdata.datashops.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bigdata.datashops.dao.data.service.AbstractMysqlPagingAndSortingQueryService;
import com.bigdata.datashops.model.pojo.job.JobInstance;

@Service
public class JobInstanceService extends AbstractMysqlPagingAndSortingQueryService<JobInstance, Integer> {
    public List<JobInstance> findReadyJob(String filters) {
        return findByQuery(filters);
    }

    public JobInstance findJobInstance(String filter) {
        return findOneByQuery(filter);
    }

    @Override
    public JobInstance save(JobInstance jobInstance) {
        return save(jobInstance);
    }

}
