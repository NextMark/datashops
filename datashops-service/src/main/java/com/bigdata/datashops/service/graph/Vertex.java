package com.bigdata.datashops.service.graph;

import java.util.Objects;

import lombok.Data;

@Data
public class Vertex {
    String id;

    String label;

    Integer type;

    String extra;

    public Vertex(String maskId, String label, Integer type, String extra) {
        this.id = maskId;
        this.label = label;
        this.type = type;
        this.extra = extra;
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
            return id.equals(vertex.id) && label.equals(vertex.label) && type.equals(vertex.type) && extra.equals(
                    vertex.extra);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, label, type, extra);
    }
}
