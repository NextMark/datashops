package com.bigdata.datashops.model.pojo.job.data;

import java.util.List;
import java.util.Objects;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;

import lombok.Data;

@Data
public class FlinkData {
    private Integer parallelism;

    private String version;

    private String name;

    private String className;

    private String yarnQueue;

    private String jobManagerMemory;

    private String taskManagerMemory;

    private Integer taskSlotNum;

    private String extension;

    private String fileName;

    private String url;

    private String kafkaServer;

    private String topic;

    private String groupId;

    private String checkpointPath;

    private String checkpointInterval;

    private String hdfsPath;

    private String ts;

    private String sql;

    public List<String> buildArgs() {
        List<String> args = Lists.newArrayList();
        args.add("run");
        args.add("-m");
        args.add("yarn-cluster");
        args.add("-d");
        if (StringUtils.isNotEmpty(name)) {
            args.add("-ynm");
            args.add(name);
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

    public List<String> buildKafka2HdfsArgs() {
        List<String> args = Lists.newArrayList();
        if (StringUtils.isNotEmpty(kafkaServer)) {
            args.add("--kafkaServer");
            args.add(kafkaServer);
        }
        if (StringUtils.isNotEmpty(name)) {
            args.add("--jobName");
            args.add(name);
        }
        if (StringUtils.isNotEmpty(groupId)) {
            args.add("--groupId");
            args.add(groupId);
        }
        if (StringUtils.isNotEmpty(checkpointPath)) {
            args.add("--checkpointPath");
            args.add(checkpointPath);
        }
        if (!Objects.isNull(checkpointInterval)) {
            args.add("--checkpointInterval");
            args.add(checkpointInterval);
        }
        if (StringUtils.isNotEmpty(topic)) {
            args.add("--topic");
            args.add(topic);
        }
        if (!Objects.isNull(hdfsPath)) {
            args.add("--path");
            args.add(String.valueOf(hdfsPath));
        }
        if (StringUtils.isNotEmpty(ts)) {
            args.add("--ts");
            args.add(ts);
        }
        return args;
    }

    public List<String> buildFSQLArgs() {
        List<String> args = Lists.newArrayList();
        if (StringUtils.isNotEmpty(sql)) {
            args.add("--sql");
            args.add(Base64.encodeBase64String(sql.getBytes()));
        }
        return args;
    }

}
