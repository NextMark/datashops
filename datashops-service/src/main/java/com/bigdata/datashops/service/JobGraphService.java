package com.bigdata.datashops.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.bigdata.datashops.dao.data.domain.PageRequest;
import com.bigdata.datashops.dao.data.service.AbstractMysqlPagingAndSortingQueryService;
import com.bigdata.datashops.model.enums.NodeType;
import com.bigdata.datashops.model.pojo.job.Edge;
import com.bigdata.datashops.model.pojo.job.Job;
import com.bigdata.datashops.model.pojo.job.JobDependency;
import com.bigdata.datashops.model.pojo.job.JobGraph;
import com.bigdata.datashops.model.pojo.job.JobRelation;
import com.bigdata.datashops.model.vo.VoJobNode;
import com.google.common.collect.Lists;

@Service
public class JobGraphService extends AbstractMysqlPagingAndSortingQueryService<JobGraph, Integer> {
    @Autowired
    private JobService jobService;

    @Autowired
    private JobDependencyService jobDependencyService;

    @Autowired
    private JobRelationService jobRelationService;

    public JobGraph getJobGraph(Integer id) {
        return findById(id);
    }

    public JobGraph getJobGraphByMaskId(String id) {
        return findOneByQuery("maskId=" + id);
    }

    public Page<JobGraph> getJobGraphList(PageRequest pageRequest) {
        return pageByQuery(pageRequest);
    }

    public void fillJobWithDependency(JobGraph jobGraph) {
        List<VoJobNode> nodes = Lists.newArrayList();
        List<JobRelation> relations = jobRelationService.getJobRelations("graphMaskId=" + jobGraph.getMaskId());
        for (JobRelation relation : relations) {
            VoJobNode node = new VoJobNode();
            if (relation.getNodeType() == NodeType.GRAPH.getCode()) {
                JobGraph graph = getJobGraphByMaskId(relation.getJobMaskId());
                node.setName(graph.getName());
                node.setIco("el-icon-my-graph");
                node.setType(NodeType.GRAPH.getCode());
                node.setId(relation.getJobMaskId());
            }
            if (relation.getNodeType() == NodeType.JOB.getCode()) {
                Job job = jobService.getJobByMaskId(relation.getJobMaskId());
                node.setName(job.getName());
                node.setIco(job.getIco());
                node.setType(NodeType.JOB.getCode());
                node.setId(relation.getJobMaskId());
            }
            node.setTop(relation.getTopPos());
            node.setLeft(relation.getLeftPos());
            nodes.add(node);
        }

        List<Edge> edges = Lists.newArrayList();
        for (VoJobNode node : nodes) {
            String filter = "targetId=" + node.getId() + ";graphId=" + jobGraph.getMaskId();
            List<JobDependency> jobDependencies = jobDependencyService.getJobDependency(filter);
            if (jobDependencies.size() == 0) {
                Edge edge = new Edge();
                edge.setFrom("1-+1");
                edge.setTo(node.getId());
                edges.add(edge);
            } else {
                for (JobDependency jobDependency : jobDependencies) {
                    Edge edge = new Edge();
                    edge.setFrom(String.valueOf(jobDependency.getSourceId()));
                    edge.setTo(String.valueOf(jobDependency.getTargetId()));
                    if (!edges.contains(edge)) {
                        edges.add(edge);
                    }
                }
            }

            filter = "sourceId=" + node.getId() + ";graphMaskId=" + jobGraph.getMaskId();
            jobDependencies = jobDependencyService.getJobDependency(filter);
            if (jobDependencies.size() == 0) {
                Edge edge = new Edge();
                edge.setFrom(node.getId());
                edge.setTo("1-+2");
                edges.add(edge);
            } else {
                for (JobDependency jobDependency : jobDependencies) {
                    Edge edge = new Edge();
                    edge.setFrom(String.valueOf(jobDependency.getSourceId()));
                    edge.setTo(String.valueOf(jobDependency.getTargetId()));
                    if (!edges.contains(edge)) {
                        edges.add(edge);
                    }
                }
            }
        }
        VoJobNode root = new VoJobNode();
        root.setId("1-+1");
        root.setName("root");
        root.setIco("el-icon-my-start");
        root.setLeft("333px");
        root.setTop("0px");
        root.setType(NodeType.JOB.getCode());
        nodes.add(root);

        VoJobNode end = new VoJobNode();
        end.setId("1-+2");
        end.setName("end");
        end.setIco("el-icon-my-end");
        end.setLeft("333px");
        end.setTop("470px");
        end.setType(NodeType.JOB.getCode());
        nodes.add(end);

        jobGraph.setNodeList(nodes);
        jobGraph.setLineList(edges);
    }
}
