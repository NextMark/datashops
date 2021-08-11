package com.bigdata.datashops.service.graph;

import java.util.Objects;

import lombok.Data;

@Data
public class Vertex {
    String maskId;

    String name;

    String bizTime;

    Integer type;

    public Vertex(String maskId, String name, String bizTime, Integer type) {
        this.maskId = maskId;
        this.name = name;
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
            return maskId.equals(vertex.maskId) && name.equals(vertex.name) && bizTime.equals(vertex.bizTime)
                           && type.equals(vertex.type);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(maskId, name, bizTime, type);
    }
}
