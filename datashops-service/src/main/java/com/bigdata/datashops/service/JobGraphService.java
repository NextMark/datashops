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

        List<Map<String, Object>> dependencies = Lists.newArrayList();
        for (Job job : jobs) {
            String filter = "targetId=" + job.getId();
            List<JobDependency> jobDependencies = jobDependencyService.getJobDependency(filter);
            if (jobDependencies.size() == 0) {
                Map<String, Object> line = Maps.newHashMap();
                line.put("from", -1);
                line.put("to", job.getId());
                dependencies.add(line);
            }
            filter = "sourceId=" + job.getId();
            jobDependencies = jobDependencyService.getJobDependency(filter);
            if (jobDependencies.size() == 0) {
                Map<String, Object> line = Maps.newHashMap();
                line.put("from", job.getId());
                line.put("to", -2);
                dependencies.add(line);
            }

            for (JobDependency jobDependency : jobDependencies) {
                Map<String, Object> line = Maps.newHashMap();
                line.put("from", job.getId());
                line.put("to", jobDependency.getTargetId());
                dependencies.add(line);
            }
        }
        Job root = new Job();
        root.setId(-1);
        root.setName("root");
        root.setGraphId(jobGraph.getId());
        root.setIco("el-icon-my-start");
        root.setLeft("10px");
        root.setTop("20px");
        jobs.add(root);

        Job end = new Job();
        end.setId(-2);
        end.setName("end");
        end.setGraphId(jobGraph.getId());
        end.setIco("el-icon-my-end");
        end.setLeft("600px");
        end.setTop("20px");
        jobs.add(end);

        jobGraph.setNodeList(jobs);
        jobGraph.setLineList(dependencies);
    }
}
