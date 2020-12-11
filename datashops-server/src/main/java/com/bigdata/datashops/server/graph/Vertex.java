package com.bigdata.datashops.server.graph;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Vertex {
    private String jobInstanceId;

    private boolean beingVisited;

    private boolean visited;

    // 入度
    private int in;

    // 出度
    private int out;

    private List<Vertex> adjacencyList;

    public Vertex(String jobInstanceId) {
        this.jobInstanceId = jobInstanceId;
        this.adjacencyList = new ArrayList<>();
    }

    public void addNeighbor(Vertex adjacent) {
        this.adjacencyList.add(adjacent);
    }
}
