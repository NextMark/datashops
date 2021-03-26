package com.bigdata.datashops.service;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.bigdata.datashops.dao.data.domain.PageRequest;
import com.bigdata.datashops.dao.data.service.AbstractMysqlPagingAndSortingQueryService;
import com.bigdata.datashops.model.pojo.hadoop.ResourceFile;

@Service
public class ResourceFileService extends AbstractMysqlPagingAndSortingQueryService<ResourceFile, Integer> {
    public Page<ResourceFile> getList(PageRequest pageRequest) {
        return pageByQuery(pageRequest);
    }
}
