package com.bigdata.datashops.service;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.bigdata.datashops.dao.data.domain.PageRequest;
import com.bigdata.datashops.dao.data.service.AbstractMysqlPagingAndSortingQueryService;
import com.bigdata.datashops.model.pojo.DataSource;

@Service
public class DataSourceService extends AbstractMysqlPagingAndSortingQueryService<DataSource, Integer> {
    public DataSource findOne(String filter) {
        return findOneByQuery(filter);
    }

    public void saveEntity(DataSource entity) {
        save(entity);
    }

    public Page<DataSource> getList(PageRequest pageRequest) {
        return pageByQuery(pageRequest);
    }
}
