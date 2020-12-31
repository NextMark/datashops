package com.bigdata.datashops.dao.job;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.bigdata.datashops.model.pojo.job.JobInstance;

public interface JobInstanceDao extends PagingAndSortingRepository<JobInstance, Integer> {
}
