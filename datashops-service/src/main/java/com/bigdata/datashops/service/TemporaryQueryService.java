package com.bigdata.datashops.service;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.bigdata.datashops.dao.data.domain.PageRequest;
import com.bigdata.datashops.dao.data.service.AbstractMysqlPagingAndSortingQueryService;
import com.bigdata.datashops.model.pojo.job.TemporaryQuery;

@Service
public class TemporaryQueryService extends AbstractMysqlPagingAndSortingQueryService<TemporaryQuery, Integer> {
    public Page<TemporaryQuery> getList(PageRequest pageRequest) {
        return pageByQuery(pageRequest);
    }

}
