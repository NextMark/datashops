package com.bigdata.datashops.model.pojo.job;

import java.util.Objects;

import lombok.Data;

@Data
public class Edge {
    private Integer id;

    private String from;

    private String to;

    private String label;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Edge edge = (Edge) o;
        return id.equals(edge.id) && from.equals(edge.from) && to.equals(edge.to) && label.equals(edge.label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getFrom(), getTo(), getLabel());
    }
}
