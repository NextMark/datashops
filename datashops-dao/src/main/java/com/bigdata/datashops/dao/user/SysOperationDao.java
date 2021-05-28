package com.bigdata.datashops.dao.user;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.bigdata.datashops.model.pojo.user.SysOperation;

public interface SysOperationDao extends PagingAndSortingRepository<SysOperation, Integer> {
}
