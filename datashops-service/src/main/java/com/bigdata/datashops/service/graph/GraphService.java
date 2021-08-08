package com.bigdata.datashops.service.graph;

import java.util.List;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.springframework.stereotype.Service;

import com.bigdata.datashops.model.pojo.job.JobDependency;
import com.bigdata.datashops.model.pojo.job.JobInstance;
import com.bigdata.datashops.service.BaseService;

import lombok.Data;

@Service
public class GraphService extends BaseService {

    public void getDescendants(String maskId, int version) {

    }

    public void getFlow(String instanceId) {
        DirectedAcyclicGraph<String, DefaultEdge> directedGraph = new DirectedAcyclicGraph<>(DefaultEdge.class);

        JobInstance instance = jobInstanceService.findByInstanceId(instanceId);
        List<JobDependency> upstream = jobDependencyService.findByTargetId(instance.getMaskId());

        List<JobDependency> downstream = jobDependencyService.findBySourceId(instance.getMaskId());


    }

    @Data
    class Vertex {
        String name;

        String baseTime;

        @Override
        public String toString() {
            return name + "^_^" + baseTime;
        }

        public Vertex parse(String s) {
            Vertex vertex = new Vertex();
            String[] fields = s.split("^_^");
            vertex.name = fields[0];
            vertex.baseTime = fields[1];
            return vertex;
        }
    }
}
