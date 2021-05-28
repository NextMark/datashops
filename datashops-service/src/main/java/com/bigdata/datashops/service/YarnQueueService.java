package com.bigdata.datashops.service;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.bigdata.datashops.dao.data.domain.PageRequest;
import com.bigdata.datashops.dao.data.service.AbstractMysqlPagingAndSortingQueryService;
import com.bigdata.datashops.model.pojo.YarnQueue;

@Service
public class YarnQueueService extends AbstractMysqlPagingAndSortingQueryService<YarnQueue, Integer> {
    public YarnQueue findOne(String filter) {
        return findOneByQuery(filter);
    }

    public void saveEntity(YarnQueue entity) {
        save(entity);
    }

    public Page<YarnQueue> getList(PageRequest pageRequest) {
        return pageByQuery(pageRequest);
    }
}
