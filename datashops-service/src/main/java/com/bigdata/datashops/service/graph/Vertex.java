package com.bigdata.datashops.service.graph;

import java.util.Objects;

import lombok.Data;

@Data
public class Vertex {
    String id;

    String label;

    String bizTime;

    Integer type;

    public Vertex(String maskId, String label, String bizTime, Integer type) {
        this.id = maskId;
        this.label = label;
        this.bizTime = bizTime;
        this.type = type;
    }

    public Vertex() {

    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (this == o) {
            return true;
        }
        if (o instanceof Vertex) {
            Vertex vertex = (Vertex) o;
            return id.equals(vertex.id) && label.equals(vertex.label) && bizTime.equals(vertex.bizTime)
                           && type.equals(vertex.type);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, label, bizTime, type);
    }
}
