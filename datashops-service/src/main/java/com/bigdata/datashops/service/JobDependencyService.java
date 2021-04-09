package com.bigdata.datashops.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bigdata.datashops.dao.data.service.AbstractMysqlPagingAndSortingQueryService;
import com.bigdata.datashops.model.pojo.job.Edge;
import com.bigdata.datashops.model.pojo.job.Job;
import com.bigdata.datashops.model.pojo.job.JobDependency;
import com.bigdata.datashops.model.pojo.job.Node;
import com.bigdata.datashops.model.vo.VoJobDependency;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

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
                    VoJobDependency.builder().id(dependency.getId()).name(job.getName()).offset(dependency.getOffset())
                            .owner(job.getOwner()).schedulingPeriod(job.getSchedulingPeriod())
                            .type(dependency.getType()).jobType(job.getType()).sourceId(dependency.getSourceId())
                            .build();
            vo.add(v);
        }
        return vo;
    }

    public Map<String, Object> getJobDependencyGraph(Integer id) {
        Map<String, Object> result = Maps.newHashMap();
        List<Edge> edges = Lists.newArrayList();
        List<Node> nodes = Lists.newArrayList();
        Job job = jobService.getJob(id);
        Node node = new Node();
        node.setLabel(job.getName());
        node.setId(id.toString());
        nodes.add(node);

        findPre(id, edges, nodes);
        findPost(id, edges, nodes);
        result.put("nodes", nodes);
        result.put("edges", edges);
        return result;
    }

    private void findPre(Integer id, List<Edge> edges, List<Node> nodes) {
        List<JobDependency> pre = getJobDependency("targetId=" + id);
        if (pre.size() > 0) {
            for (JobDependency dependency : pre) {
                Job job = jobService.getJob(dependency.getSourceId());
                Node node = new Node();
                node.setId(dependency.getSourceId().toString());
                node.setLabel(job.getName());
                nodes.add(node);

                Edge edge = new Edge();
                edge.setFrom(dependency.getSourceId().toString());
                edge.setTo(dependency.getTargetId().toString());
                edge.setLabel(
                        String.format("%s: [%s]", dependency.getType() == 1 ? "集合" : "区间", dependency.getOffset()));
                edges.add(edge);
                if (id.equals(dependency.getSourceId())) {
                    continue;
                }
                findPre(dependency.getSourceId(), edges, nodes);
            }
        }
    }

    private void findPost(Integer id, List<Edge> edges, List<Node> nodes) {
        List<JobDependency> pre = getJobDependency("sourceId=" + id);
        if (pre.size() > 0) {
            for (JobDependency dependency : pre) {
                Job job = jobService.getJob(dependency.getTargetId());
                Node node = new Node();
                node.setId(dependency.getTargetId().toString());
                node.setLabel(job.getName());
                nodes.add(node);

                Edge edge = new Edge();
                edge.setFrom(dependency.getSourceId().toString());
                edge.setTo(dependency.getTargetId().toString());
                edge.setLabel(
                        String.format("%s: [%s]", dependency.getType() == 1 ? "集合" : "区间", dependency.getOffset()));
                edges.add(edge);
                if (id.equals(dependency.getTargetId())) {
                    continue;
                }
                findPost(dependency.getTargetId(), edges, nodes);
            }
        }
    }
}
