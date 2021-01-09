package com.bigdata.datashops.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bigdata.datashops.dao.data.service.AbstractMysqlPagingAndSortingQueryService;
import com.bigdata.datashops.model.pojo.job.JobDependency;

@Service
public class JobDependencyService extends AbstractMysqlPagingAndSortingQueryService<JobDependency, Integer> {
    public List<JobDependency> getJobDependency(String filter) {
        return findByQuery(filter);
    }
}
