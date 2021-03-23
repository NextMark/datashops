package com.bigdata.datashops.model.pojo.job.data;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;

import lombok.Data;

@Data
public class FlinkData {
    private Integer parallelism;

    private String version;

    private String yarnAppName;

    private String className;

    private String yarnQueue;

    private String jobManagerMemory;

    private String taskManagerMemory;

    private Integer taskSlotNum;

    private String extension;

    private String fileName;

    private String url;

    public List<String> buildArgs() {
        List<String> args = Lists.newArrayList();
        args.add("run");
        args.add("-m");
        args.add("yarn-cluster");
        args.add("-d");
        if (StringUtils.isNotEmpty(yarnAppName)) {
            args.add("-ynm");
            args.add(yarnAppName);
        }
        if (StringUtils.isNotEmpty(yarnQueue)) {
            args.add("-yq");
            args.add(yarnQueue);
        }
        if (StringUtils.isNotEmpty(jobManagerMemory)) {
            args.add("-yjm");
            args.add(jobManagerMemory);
        }
        if (StringUtils.isNotEmpty(taskManagerMemory)) {
            args.add("-ytm");
            args.add(taskManagerMemory);
        }
        if (!Objects.isNull(taskSlotNum)) {
            args.add("-ys");
            args.add(String.valueOf(taskSlotNum));
        }
        if (StringUtils.isNotEmpty(className)) {
            args.add("-c");
            args.add(className);
        }
        if (!Objects.isNull(parallelism)) {
            args.add("-p");
            args.add(String.valueOf(parallelism));
        }
        if (StringUtils.isNotEmpty(extension)) {
            args.add(extension);
        }
        args.add("-j");
        return args;
    }

}
