package com.bigdata.datashops.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bigdata.datashops.dao.data.service.AbstractMysqlPagingAndSortingQueryService;
import com.bigdata.datashops.model.pojo.job.Job;
import com.bigdata.datashops.model.pojo.job.JobDependency;
import com.bigdata.datashops.model.vo.VoJobDependency;
import com.google.common.collect.Lists;

@Service
public class JobDependencyService extends AbstractMysqlPagingAndSortingQueryService<JobDependency, Integer> {
    @Autowired
    private JobService jobService;

    public List<JobDependency> getJobDependency(String filter) {
        return findByQuery(filter);
    }

    public JobDependency getOne(String filter) {
        return findOneByQuery(filter);
    }

    public List<VoJobDependency> fillJobInfo(List<JobDependency> dependencies) {
        List<VoJobDependency> vo = Lists.newArrayList();
        for (JobDependency dependency : dependencies) {
            Job job = jobService.getJob(dependency.getSourceId());
            VoJobDependency v =
                    VoJobDependency.builder().name(job.getName()).offset(dependency.getOffset()).owner(job.getOwner())
                            .type(job.getType()).sourceId(dependency.getSourceId()).build();
            vo.add(v);
        }
        return vo;
    }
}
