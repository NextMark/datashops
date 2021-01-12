package com.bigdata.datashops.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.bigdata.datashops.common.Constants;
import com.bigdata.datashops.common.utils.JSONUtils;
import com.bigdata.datashops.dao.data.domain.PageRequest;
import com.bigdata.datashops.dao.data.service.AbstractMysqlPagingAndSortingQueryService;
import com.bigdata.datashops.model.enums.JobInstanceType;
import com.bigdata.datashops.model.pojo.job.Job;
import com.bigdata.datashops.model.pojo.job.JobDependency;
import com.bigdata.datashops.model.pojo.job.JobGraph;
import com.bigdata.datashops.model.vo.VoJobNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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

    public List<JobGraph> getJobGraphs(String filter) {
        return findByQuery(filter);
    }

    public Page<JobGraph> getJobGraphList(PageRequest pageRequest) {
        return pageByQuery(pageRequest);
    }

    public void fillJobWithDependency(JobGraph jobGraph) {
        List<String> jobIds = Arrays.asList(jobGraph.getJobIds().split(Constants.SEPARATOR_COMMA));
        List<VoJobNode> nodes = Lists.newArrayList();

        String sb = "id=" + StringUtils.join(jobIds, Constants.SEPARATOR_COMMA) + ";status=1";
        List<Job> jobs = jobService.findJobs(sb);
        for (Job job : jobs) {
            VoJobNode node = new VoJobNode();
            node.setType(JobInstanceType.JOB.getCode());
            node.setName(job.getName());
            node.setIco(job.getIco());
            node.setId(job.getId());
            node.setNodeId(JobInstanceType.JOB.getCode() + Constants.SEPARATOR_HYPHEN + job.getId());
            node.setTop(job.getTop());
            node.setLeft(job.getLeft());
            nodes.add(node);
        }

        ObjectNode objectNode = JSONUtils.parseObject(jobGraph.getGraphNodes());
        List<JsonNode> graphNodes = objectNode.findValues("graphNodes");
        graphNodes.forEach(n -> {
            VoJobNode node = new VoJobNode();
            node.setId(n.get("id").asInt());
            node.setNodeId(JobInstanceType.GRAPH.getCode() + Constants.SEPARATOR_HYPHEN + n.get("id").asInt());
            node.setIco(n.get("ico").asText());
            node.setLeft(n.get("left").asText());
            node.setTop(n.get("top").asText());
            node.setName(n.get("name").asText());
            node.setType(JobInstanceType.GRAPH.getCode());
            nodes.add(node);
        });

        List<Map<String, Object>> dependencies = Lists.newArrayList();
        for (VoJobNode node : nodes) {
            String filter = "targetId=" + node.getId() + ";graphId=" + jobGraph.getId();
            List<JobDependency> jobDependencies = jobDependencyService.getJobDependency(filter);
            if (jobDependencies.size() == 0) {
                Map<String, Object> line = Maps.newHashMap();
                line.put("from", -1);
                line.put("to", node.getNodeId());
                dependencies.add(line);
            }
            filter = "sourceId=" + node.getId() + ";graphId=" + jobGraph.getId();
            jobDependencies = jobDependencyService.getJobDependency(filter);
            if (jobDependencies.size() == 0) {
                Map<String, Object> line = Maps.newHashMap();
                line.put("from", node.getNodeId());
                line.put("to", -2);
                dependencies.add(line);
            }

            for (JobDependency jobDependency : jobDependencies) {
                Map<String, Object> line = Maps.newHashMap();
                line.put("from", node.getNodeId());
                line.put("to",
                        jobDependency.getDependType() + Constants.SEPARATOR_HYPHEN + jobDependency.getTargetId());
                line.put("offset", jobDependency.getOffset());
                dependencies.add(line);
            }
        }
        VoJobNode root = new VoJobNode();
        root.setId(-1);
        root.setNodeId("1--1");
        root.setName("root");
        root.setIco("el-icon-my-start");
        root.setLeft("333px");
        root.setTop("0px");
        root.setType(1);
        nodes.add(root);

        VoJobNode end = new VoJobNode();
        end.setId(-2);
        end.setNodeId("1--2");
        end.setName("end");
        end.setIco("el-icon-my-end");
        end.setLeft("333px");
        end.setTop("470px");
        end.setType(1);
        nodes.add(end);

        jobGraph.setNodeList(nodes);
        jobGraph.setLineList(dependencies);
    }
}
