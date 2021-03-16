package com.bigdata.datashops.model.pojo.job.data;

import lombok.Data;

@Data
public class SparkData {
    private String className;

    private String name;

    private String queue;

    private String driverMemory;

    private String executorMemory;

    private String executorCore;

    private String extension;

}
