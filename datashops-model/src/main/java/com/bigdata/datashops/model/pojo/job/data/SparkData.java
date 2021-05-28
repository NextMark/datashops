package com.bigdata.datashops.model.pojo.job.data;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;

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

    private String fileName;

    private String url;

    public List<String> buildArgs() {
        List<String> args = Lists.newArrayList();
        args.add("--master");
        args.add("yarn");
        args.add("--deploy-mode");
        args.add("cluster");
        args.add("--class");
        args.add(className);
        if (StringUtils.isNotEmpty(driverMemory)) {
            args.add("--driver-memory");
            args.add(driverMemory);
        }
        if (StringUtils.isNotEmpty(executorMemory)) {
            args.add("--executor-memory");
            args.add(executorCore);
        }
        if (StringUtils.isNotEmpty(executorCore)) {
            args.add("--executor-core");
            args.add(executorCore);
        }
        if (StringUtils.isNotEmpty(queue)) {
            args.add("--queue");
            args.add(queue);
        }
        if (StringUtils.isNotEmpty(extension)) {
            args.add(extension);
        }
        return args;
    }

}
