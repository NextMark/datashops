package com.bigdata.datashops.dao.job;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.bigdata.datashops.model.pojo.DataSource;

public interface DataSourceDao extends PagingAndSortingRepository<DataSource, Integer> {
}
