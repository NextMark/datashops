package com.bigdata.datashops.service;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.bigdata.datashops.dao.data.domain.PageRequest;
import com.bigdata.datashops.dao.data.service.AbstractMysqlPagingAndSortingQueryService;
import com.bigdata.datashops.model.pojo.user.SysOperation;

@Service
public class SysOperationService extends AbstractMysqlPagingAndSortingQueryService<SysOperation, Integer> {
    public Page<SysOperation> getList(PageRequest pageRequest) {
        return pageByQuery(pageRequest);
    }

}
