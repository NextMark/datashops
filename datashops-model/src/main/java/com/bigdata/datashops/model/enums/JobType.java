package com.bigdata.datashops.model.enums;

import java.util.HashMap;

public enum JobType {
    HIVE(0, "hive"), SHELL(1, "bash"), SPARK(2, "spark"), FLINK(3, "flink"), CLICK_HOUSE(4, "clickhouse"),
    FSQL(5, "fsql"), MYSQL(6, "mysql"), PYTHON(11, "python"),

    KAFKA_2_HIVE(7, "kafka2hive"), KAFKA_2_HDFS(8, "kafka2hdfs"), HIVE_2_MYSQL(9, "kafka2mysql"),
    MYSQL_2_HIVE(10, "mysql2hive");

    JobType(int code, String name) {
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

    private static HashMap<Integer, JobType> JOB_TYPE_MAP = new HashMap<>();

    static {
        for (JobType jobType : JobType.values()) {
            JOB_TYPE_MAP.put(jobType.getCode(), jobType);
        }
    }

    public static JobType of(int type) {
        if (JOB_TYPE_MAP.containsKey(type)) {
            return JOB_TYPE_MAP.get(type);
        }
        throw new IllegalArgumentException("Invalid type : " + type);
    }
}
