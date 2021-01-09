package com.bigdata.datashops.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.bigdata.datashops.dao.data.domain.PageRequest;
import com.bigdata.datashops.dao.data.service.AbstractMysqlPagingAndSortingQueryService;
import com.bigdata.datashops.model.pojo.job.Job;
import com.bigdata.datashops.model.pojo.job.JobDependency;
import com.bigdata.datashops.model.pojo.job.JobGraph;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Service
public class JobGraphService extends AbstractMysqlPagingAndSortingQueryService<JobGraph, Integer> {
    @Autowired
    private JobService jobService;

    @Autowired
    private JobDependencyService jobDependencyService;

    public JobGraph getJobGraph(Integer id) {
        return findById(id);
    }

    public Page<JobGraph> getJobGraphList(PageRequest pageRequest) {
        return pageByQuery(pageRequest);
    }

    public void fillJobWithDependency(JobGraph jobGraph) {
        String sb = "graphId=" + jobGraph.getId() + ";status=1";
        List<Job> jobs = jobService.findJobs(sb);
        jobGraph.setNodeList(jobs);

        List<Map<String, Object>> dependencies = Lists.newArrayList();
        for (Job job : jobs) {
            String filter = "sourceId=" + job.getId();
            List<JobDependency> jobDependencies = jobDependencyService.getJobDependency(filter);

            for (JobDependency jobDependency : jobDependencies) {
                Map<String, Object> line = Maps.newHashMap();
                line.put("from", job.getId());
                line.put("to", jobDependency.getTargetId());
                dependencies.add(line);
            }
        }
        jobGraph.setLineList(dependencies);
    }
}
