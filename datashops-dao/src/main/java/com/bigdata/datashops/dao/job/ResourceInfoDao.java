package com.bigdata.datashops.dao.job;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.bigdata.datashops.model.pojo.hadoop.ResourceInfo;

public interface ResourceInfoDao extends PagingAndSortingRepository<ResourceInfo, Integer> {
}
