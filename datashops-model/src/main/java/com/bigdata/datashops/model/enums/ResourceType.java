package com.bigdata.datashops.model.enums;

import java.util.HashMap;

public enum ResourceType {
    SPARK_JAR(0, "spark jar"), FLINK_JAR(1, "flink jar"), UDF(2, "udf"), UDAF(3, "udaf"), UDTF(4, "udtf"),
    FILE(5, "file"), ZIP(6, "zip"), JAR(7, "jar");

    ResourceType(int code, String name) {
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

    private static HashMap<Integer, ResourceType> MAP = new HashMap<>();

    static {
        for (ResourceType type : ResourceType.values()) {
            MAP.put(type.getCode(), type);
        }
    }

    public static ResourceType of(int type) {
        if (MAP.containsKey(type)) {
            return MAP.get(type);
        }
        throw new IllegalArgumentException("Invalid type : " + type);
    }
}
