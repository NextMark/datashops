package com.bigdata.datashops.dao.job;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.bigdata.datashops.model.pojo.hadoop.ResourceFile;

public interface ResourceFileDao extends PagingAndSortingRepository<ResourceFile, Integer> {
}
