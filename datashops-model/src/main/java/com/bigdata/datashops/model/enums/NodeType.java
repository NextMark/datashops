package com.bigdata.datashops.model.enums;

import java.util.HashMap;

public enum NodeType {
    GRAPH(0, "graph"), JOB(1, "job");

    NodeType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    private final int code;
    private final String name;

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    private static HashMap<Integer, NodeType> MAP = new HashMap<>();

    static {
        for (NodeType type : NodeType.values()) {
            MAP.put(type.getCode(), type);
        }
    }

    public static NodeType of(int type) {
        if (MAP.containsKey(type)) {
            return MAP.get(type);
        }
        throw new IllegalArgumentException("Invalid type : " + type);
    }
}
