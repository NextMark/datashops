package com.bigdata.datashops.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.bigdata.datashops.dao.data.domain.PageRequest;
import com.bigdata.datashops.dao.data.service.AbstractMysqlPagingAndSortingQueryService;
import com.bigdata.datashops.model.pojo.job.Job;
import com.bigdata.datashops.model.pojo.job.JobGraph;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Service
public class JobGraphService extends AbstractMysqlPagingAndSortingQueryService<JobGraph, Integer> {
    @Autowired
    private JobService jobService;

    public JobGraph getJobGraph(Integer id) {
        return findById(id);
    }

    public Page<JobGraph> getJobGraphList(PageRequest pageRequest) {
        return pageByQuery(pageRequest);
    }

    public void fillJobWithDependency(JobGraph jobGraph) {
        String sb = "graphId=" + jobGraph.getId() + ";status=1";
        List<Job> jobs = jobService.findJobs(sb);
        jobGraph.setJobList(jobs);

        List<Map<String, Object>> dependencies = Lists.newArrayList();
        for (Job job : jobs) {

            Map<String, Object> line = Maps.newHashMap();
            line.put("from", job.getId());
        }
    }
}
