package com.bigdata.datashops.service.graph;

import java.util.Date;
import java.util.List;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.springframework.stereotype.Service;

import com.bigdata.datashops.model.pojo.job.Job;
import com.bigdata.datashops.model.pojo.job.JobDependency;
import com.bigdata.datashops.model.pojo.job.JobInstance;
import com.bigdata.datashops.service.BaseService;
import com.bigdata.datashops.service.utils.CronHelper;

import lombok.Data;

@Service
public class GraphService extends BaseService {

    public void getDescendants(String maskId, int version) {

    }

    public void getFlow(String instanceId) {
        DirectedAcyclicGraph<String, DefaultEdge> directedGraph = new DirectedAcyclicGraph<>(DefaultEdge.class);

        JobInstance instance = jobInstanceService.findByInstanceId(instanceId);
        Job job = jobService.getOnlineJobByMaskId(instance.getMaskId());
        directedGraph.addVertex(new Vertex(instance, job.getName()).toString());
        List<JobDependency> upstream = jobDependencyService.findByTargetId(instance.getMaskId());

        List<JobDependency> downstream = jobDependencyService.findBySourceId(instance.getMaskId());
    }

    public void buildUpstreamInstance(DirectedAcyclicGraph<String, DefaultEdge> dag, JobInstance instance) {
        Job job = jobService.getOnlineJobByMaskId(instance.getMaskId());
        List<JobDependency> upstreamDeps = jobDependencyService.findByTargetId(instance.getMaskId());
        for (JobDependency dependency : upstreamDeps) {

            Job upstreamJob = jobService.getOnlineJobByMaskId(dependency.getSourceId());
//            Date sourceDate = CronHelper.getOffsetTriggerTime(upstreamJob.getCronExpression(), instance.getBizTime(),
//                    dependency.getOffset());


//            JobInstance tmpUpstream = new JobInstance();
//
//            Vertex upstream = new Vertex(tmpUpstream, upstreamJob.getName());
//            Vertex downstream = new Vertex(instance, job.getName());
//
//            dag.addVertex(upstream.toString());
//            dag.addEdge(upstream.toString(), downstream.toString());
//            buildUpstreamInstance(dag, tmpUpstream);
        }
    }

    public void buildDownstreamInstance() {

    }

    @Data
    class Vertex {
        String name;

        Date bizTime;

        Integer type;

        public Vertex(JobInstance instance, String name) {
            this.name = name;
            bizTime = instance.getBizTime();
            type = instance.getType();
        }

        public Vertex() {

        }

        @Override
        public String toString() {
            return name + "^_^" + bizTime;
        }

        public Vertex parse(String s) {
            Vertex vertex = new Vertex();
            Object[] fields = s.split("^_^");
            vertex.name = (String) fields[0];
            vertex.bizTime = (Date) fields[1];
            return vertex;
        }
    }
}
