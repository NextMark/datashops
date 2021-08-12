package com.bigdata.datashops.service.graph;

import java.util.List;
import java.util.Set;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedPseudograph;
import org.springframework.stereotype.Service;

import com.bigdata.datashops.model.pojo.job.Edge;
import com.bigdata.datashops.model.pojo.job.JobDependency;
import com.bigdata.datashops.service.BaseService;

@Service
public class GraphService extends BaseService {

    public void buildGraph(DirectedWeightedPseudograph<String, DefaultWeightedEdge> dag, Set<Edge> edges,
                           Set<String> vertexSet, String maskId) {
        List<JobDependency> upstream = jobDependencyService.findByTargetId(maskId);
        List<JobDependency> downstream = jobDependencyService.findBySourceId(maskId);
        upstream.addAll(downstream);
        vertexSet.add(maskId);

        for (JobDependency dep : upstream) {
            if (!dag.containsVertex(dep.getSourceId())) {
                dag.addVertex(dep.getSourceId());
            }
            if (!dag.containsVertex(dep.getTargetId())) {
                dag.addVertex(dep.getTargetId());
            }

            Edge e = new Edge();
            e.setFrom(dep.getSourceId());
            e.setTo(dep.getTargetId());
            String label = null;
            if (dep.getType() == 1) {
                label = "集合: [" + dep.getOffset() + "]";
            }
            if (dep.getType() == 2) {
                label = "区间: [" + dep.getOffset() + "]";
            }
            e.setLabel(label);
            edges.add(e);
            //            double weight = Double.parseDouble(dep.getOffset());
            Graphs.addEdge(dag, dep.getSourceId(), dep.getTargetId(), 1);

            String[] data = new String[] {dep.getSourceId(), dep.getTargetId()};
            for (String id : data) {
                if (id.equals(maskId)) {
                    continue;
                }
                if (!vertexSet.contains(id)) {
                    vertexSet.add(id);
                    buildGraph(dag, edges, vertexSet, id);
                }
            }
        }
    }
}
