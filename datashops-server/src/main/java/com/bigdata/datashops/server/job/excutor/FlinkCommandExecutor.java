package com.bigdata.datashops.server.job.excutor;

import java.util.List;
import java.util.Objects;

import com.bigdata.datashops.common.utils.JSONUtils;
import com.bigdata.datashops.model.pojo.job.data.FlinkData;
import com.bigdata.datashops.server.job.JobContext;

public class FlinkCommandExecutor extends CommandExecutor {
    public FlinkCommandExecutor(JobContext jobContext) {
        super(jobContext);
    }

    @Override
    public String buildCommandFilePath() {
        return null;
    }

    @Override
    public String commandInterpreter() {
        return "flink";
    }

    @Override
    public List<String> commandArgs() {
        FlinkData flinkData = JSONUtils.parseObject(jobContext.getJobInstance().getData(), FlinkData.class);
        return Objects.requireNonNull(flinkData).buildArgs();
    }
}
