package com.bigdata.datashops.dao.job;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.bigdata.datashops.model.pojo.job.TemporaryQuery;

public interface TemporaryQueryDao extends PagingAndSortingRepository<TemporaryQuery, Integer> {
}
