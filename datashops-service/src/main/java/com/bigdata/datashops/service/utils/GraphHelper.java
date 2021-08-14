package com.bigdata.datashops.service.utils;

import java.util.Map;
import java.util.Set;

import org.jgrapht.graph.DirectedWeightedPseudograph;

import com.bigdata.datashops.common.utils.JSONUtils;
import com.bigdata.datashops.model.pojo.job.Edge;
import com.bigdata.datashops.model.pojo.job.RelationshipEdge;
import com.bigdata.datashops.service.graph.Vertex;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class GraphHelper {
    public static Map<String, Object> parseToGraph(DirectedWeightedPseudograph<String, RelationshipEdge> dag) {
        Set<Edge> edges = Sets.newHashSet();
        Set<Vertex> vertexSet = Sets.newHashSet();
        for (String s : dag.vertexSet()) {
            Vertex vertex = JSONUtils.parseObject(s, Vertex.class);
            vertexSet.add(vertex);
        }
        for (RelationshipEdge relationshipEdge : dag.edgeSet()) {
            Edge edge = new Edge();
            Vertex from = JSONUtils.parseObject(dag.getEdgeSource(relationshipEdge), Vertex.class);
            Vertex to = JSONUtils.parseObject(dag.getEdgeTarget(relationshipEdge), Vertex.class);
            edge.setFrom(from.getId());
            edge.setTo(to.getId());
            edge.setLabel(relationshipEdge.getLabel());
            edges.add(edge);
        }
        Map<String, Object> res = Maps.newHashMap();
        res.put("edges", edges);
        res.put("nodes", vertexSet);
        return res;
    }
}
