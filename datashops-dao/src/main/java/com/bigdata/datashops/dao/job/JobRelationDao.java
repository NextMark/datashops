package com.bigdata.datashops.dao.job;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.bigdata.datashops.model.pojo.job.JobRelation;

public interface JobRelationDao extends PagingAndSortingRepository<JobRelation, Integer> {
}
