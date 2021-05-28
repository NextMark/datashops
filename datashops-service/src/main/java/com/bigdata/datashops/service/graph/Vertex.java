package com.bigdata.datashops.service.graph;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Vertex {
    private String id;

    private String name;

    private boolean beingVisited;

    private boolean visited;

    // 入度
    private int in;

    // 出度
    private int out;

    private List<Vertex> children;

    public Vertex(String id, String name) {
        this.id = id;
        this.name = name;
        this.children = new ArrayList<>();
    }

    public void addNeighbor(Vertex adjacent) {
        this.children.add(adjacent);
    }
}
