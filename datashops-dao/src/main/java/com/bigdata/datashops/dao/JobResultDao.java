package com.bigdata.datashops.dao;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.bigdata.datashops.model.pojo.job.JobResult;

public interface JobResultDao extends PagingAndSortingRepository<JobResult, String> {
}
