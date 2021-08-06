package com.bigdata.datashops.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bigdata.datashops.dao.mapper.JobRelationMapper;
import com.bigdata.datashops.model.pojo.job.JobRelation;

@Service
public class JobRelationService {
    @Autowired
    private JobRelationMapper jobRelationMapper;

    public void save(JobRelation entity) {
        jobRelationMapper.insert(entity);
    }

    public void delete(String graphMaskId, String jobMaskId) {
        LambdaQueryWrapper<JobRelation> lqw = Wrappers.lambdaQuery();
        lqw.eq(JobRelation::getGraphMaskId, graphMaskId);
        lqw.eq(JobRelation::getJobMaskId, jobMaskId);
        jobRelationMapper.delete(lqw);
    }

    public List<JobRelation> findByGraphMaskId(String graphMaskId) {
        LambdaQueryWrapper<JobRelation> lqw = Wrappers.lambdaQuery();
        lqw.eq(JobRelation::getGraphMaskId, graphMaskId);
        return jobRelationMapper.selectList(lqw);
    }

    public JobRelation findByQuery(String graphMaskId, String jobMaskId, String nodeType) {
        LambdaQueryWrapper<JobRelation> lqw = Wrappers.lambdaQuery();
        lqw.eq(JobRelation::getGraphMaskId, graphMaskId);
        lqw.eq(JobRelation::getJobMaskId, jobMaskId);
        lqw.eq(JobRelation::getNodeType, nodeType);
        return jobRelationMapper.selectOne(lqw);
    }

}
