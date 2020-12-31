package com.bigdata.datashops.dao.job;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.bigdata.datashops.model.pojo.job.Job;

public interface JobDao extends PagingAndSortingRepository<Job, String> {
}
