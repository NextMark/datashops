package com.bigdata.datashops.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bigdata.datashops.dao.data.service.AbstractMysqlPagingAndSortingQueryService;
import com.bigdata.datashops.model.pojo.job.JobRelation;

@Service
public class JobRelationService extends AbstractMysqlPagingAndSortingQueryService<JobRelation, Integer> {
    public List<JobRelation> getJobRelations(String filter) {
        return findByQuery(filter);
    }
}
