package com.bigdata.datashops.server.job.excutor;

import java.util.List;
import java.util.Objects;

import com.bigdata.datashops.common.utils.AliyunUtils;
import com.bigdata.datashops.common.utils.JSONUtils;
import com.bigdata.datashops.model.pojo.job.data.SparkData;
import com.bigdata.datashops.server.job.JobContext;

public class SparkCommandExecutor extends CommandExecutor {
    public SparkCommandExecutor(JobContext jobContext) {
        super(jobContext);
        sparkData = JSONUtils.parseObject(jobContext.getJobInstance().getData(), SparkData.class);
    }

    private SparkData sparkData;

    @Override
    public String buildCommandFilePath() {
        return String.format("%s/%s/%s", jobContext.getExecutePath(), jobContext.getJobInstance().getInstanceId(),
                sparkData.getFileName());
    }

    @Override
    public String commandInterpreter() {
        return "spark-submit";
    }

    @Override
    public List<String> commandArgs() {
        return Objects.requireNonNull(sparkData).buildArgs();
    }

    @Override
    public void buildCommandFile() {
        AliyunUtils.download(sparkData.getUrl().replace(AliyunUtils.getLocationPrefix(), ""), buildCommandFilePath());
    }
}
