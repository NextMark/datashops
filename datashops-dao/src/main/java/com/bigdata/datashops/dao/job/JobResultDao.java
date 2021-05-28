package com.bigdata.datashops.dao.job;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.bigdata.datashops.model.pojo.job.JobResult;

public interface JobResultDao extends PagingAndSortingRepository<JobResult, Integer> {
}
