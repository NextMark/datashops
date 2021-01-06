package com.bigdata.datashops.service;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.bigdata.datashops.dao.data.domain.PageRequest;
import com.bigdata.datashops.dao.data.service.AbstractMysqlPagingAndSortingQueryService;
import com.bigdata.datashops.model.pojo.job.JobGraph;

@Service
public class JobGraphService extends AbstractMysqlPagingAndSortingQueryService<JobGraph, Integer> {
    public JobGraph getJobGraph(Integer id) {
        return findById(id);
    }

    public Page<JobGraph> getJobGraphList(PageRequest pageRequest) {
        return pageByQuery(pageRequest);
    }
}
