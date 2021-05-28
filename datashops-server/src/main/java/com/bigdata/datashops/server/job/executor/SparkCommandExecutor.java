package com.bigdata.datashops.server.job.executor;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import com.bigdata.datashops.common.utils.HadoopUtils;
import com.bigdata.datashops.common.utils.JSONUtils;
import com.bigdata.datashops.model.pojo.job.data.SparkData;
import com.bigdata.datashops.server.job.JobContext;

public class SparkCommandExecutor extends CommandExecutor {
    public SparkCommandExecutor(JobContext jobContext) {
        super(jobContext);
        sparkData = JSONUtils.parseObject(jobContext.getJobInstance().getJob().getData(), SparkData.class);
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
    public void buildCommandFile() throws IOException {
        HadoopUtils.getInstance().copyHdfsToLocal(sparkData.getUrl(), buildCommandFilePath(), false, false);

        //AliyunUtils.download(sparkData.getUrl().replace(AliyunUtils.getLocationPrefix(), ""), buildCommandFilePath());
    }
}
