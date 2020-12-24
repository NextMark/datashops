package com.bigdata.datashops.service;

import org.springframework.stereotype.Service;

import com.bigdata.datashops.dao.data.service.AbstractMysqlPagingAndSortingQueryService;
import com.bigdata.datashops.model.pojo.job.JobGraph;

@Service
public class JobGraphService extends AbstractMysqlPagingAndSortingQueryService<JobGraph, String> {
    public JobGraph getJobGraph(Integer id) {
        return findById(id.toString());
    }
}
