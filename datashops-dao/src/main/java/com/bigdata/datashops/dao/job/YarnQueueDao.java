package com.bigdata.datashops.dao.job;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.bigdata.datashops.model.pojo.YarnQueue;

public interface YarnQueueDao extends PagingAndSortingRepository<YarnQueue, Integer> {
}
