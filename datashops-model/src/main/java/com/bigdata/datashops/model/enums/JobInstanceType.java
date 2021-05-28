package com.bigdata.datashops.model.enums;

import java.util.HashMap;

public enum JobInstanceType {
    GRAPH(0, "graph"),
    JOB(1, "job");

    JobInstanceType(int code, String name) {
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

    private static HashMap<Integer, JobInstanceType> MAP = new HashMap<>();

    static {
        for (JobInstanceType type : JobInstanceType.values()) {
            MAP.put(type.getCode(), type);
        }
    }

    public static JobInstanceType of(int type) {
        if (MAP.containsKey(type)) {
            return MAP.get(type);
        }
        throw new IllegalArgumentException("Invalid type : " + type);
    }
}
