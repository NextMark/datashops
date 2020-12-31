package com.bigdata.datashops.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bigdata.datashops.dao.data.service.AbstractMysqlPagingAndSortingQueryService;
import com.bigdata.datashops.model.pojo.job.Job;

@Service
public class JobService extends AbstractMysqlPagingAndSortingQueryService<Job, Integer> {
    public Job getJob(Integer id) {
        return findById(id);
    }

    public List<Job> findJobs(String filters) {
        return findByQuery(filters);
    }

}
