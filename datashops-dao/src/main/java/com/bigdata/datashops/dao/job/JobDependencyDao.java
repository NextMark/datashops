package com.bigdata.datashops.dao.job;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.bigdata.datashops.model.pojo.job.JobDependency;

public interface JobDependencyDao extends PagingAndSortingRepository<JobDependency, Integer> {
}
