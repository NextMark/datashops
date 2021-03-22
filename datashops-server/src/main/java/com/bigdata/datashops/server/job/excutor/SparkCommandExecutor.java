package com.bigdata.datashops.server.job.excutor;

import java.util.List;
import java.util.Objects;

import com.bigdata.datashops.common.utils.JSONUtils;
import com.bigdata.datashops.model.pojo.job.data.SparkData;
import com.bigdata.datashops.server.job.JobContext;

public class SparkCommandExecutor extends CommandExecutor {
    public SparkCommandExecutor(JobContext jobContext) {
        super(jobContext);
    }

    @Override
    public String buildCommandFilePath() {
        return null;
    }

    @Override
    public String commandInterpreter() {
        return "spark-submit";
    }

    @Override
    public List<String> commandArgs() {
        SparkData sparkData = JSONUtils.parseObject(jobContext.getJobInstance().getData(), SparkData.class);
        return Objects.requireNonNull(sparkData).buildArgs();
    }
}
