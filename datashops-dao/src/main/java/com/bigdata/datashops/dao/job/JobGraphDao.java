package com.bigdata.datashops.dao.job;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.bigdata.datashops.model.pojo.job.JobGraph;

public interface JobGraphDao extends PagingAndSortingRepository<JobGraph, Integer> {
}
