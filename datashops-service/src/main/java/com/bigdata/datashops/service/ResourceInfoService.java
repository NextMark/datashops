package com.bigdata.datashops.service;

import org.springframework.stereotype.Service;

import com.bigdata.datashops.dao.data.service.AbstractMysqlPagingAndSortingQueryService;
import com.bigdata.datashops.model.pojo.hadoop.ResourceInfo;

@Service
public class ResourceInfoService extends AbstractMysqlPagingAndSortingQueryService<ResourceInfo, Integer> {
}
