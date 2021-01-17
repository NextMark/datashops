package com.bigdata.datashops.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bigdata.datashops.dao.data.service.AbstractMysqlPagingAndSortingQueryService;
import com.bigdata.datashops.model.pojo.job.JobRelation;

@Service
public class JobRelationService extends AbstractMysqlPagingAndSortingQueryService<JobRelation, Integer> {
    public List<JobRelation> getJobRelations(String filter) {
        return findByQuery(filter);
    }

    public JobRelation getJobRelation(String graphStrId, String jobStrId) {
        return findOneByQuery("graphStrId=" + graphStrId + ";jobStrId=" + jobStrId);
    }

    @Transactional
    public void delete(String graphStrId, String jobStrId) {
        deleteByQuery("graphStrId=" + graphStrId + ";jobStrId=" + jobStrId);
    }

}
