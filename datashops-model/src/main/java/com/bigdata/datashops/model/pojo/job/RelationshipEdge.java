package com.bigdata.datashops.model.pojo.job;

import org.jgrapht.graph.DefaultEdge;

public class RelationshipEdge extends DefaultEdge {
    private Integer id;

    private Integer type;

    private String value;

    public RelationshipEdge() {}

    /**
     * Constructs a relationship edge
     *
     * @param value the value of the new edge.
     */
    public RelationshipEdge(Integer id, Integer type, String value) {
        this.id = id;
        this.type = type;
        this.value = value;
    }

    public Integer getId() {
        return id;
    }

    public Integer getType() {
        return type;
    }
    /**
     * Gets the label associated with this edge.
     *
     * @return edge label
     */
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "(" + getId() + " : " + getType() + " : " + getSource() + " : " + getTarget() + " : " + value + ")";
    }
}
