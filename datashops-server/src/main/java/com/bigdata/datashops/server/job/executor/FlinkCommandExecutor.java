package com.bigdata.datashops.server.job.executor;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import com.bigdata.datashops.common.Constants;
import com.bigdata.datashops.common.utils.HadoopUtils;
import com.bigdata.datashops.common.utils.JSONUtils;
import com.bigdata.datashops.common.utils.PropertyUtils;
import com.bigdata.datashops.model.pojo.job.data.FlinkData;
import com.bigdata.datashops.server.job.JobContext;

@Deprecated
public class FlinkCommandExecutor extends CommandExecutor {
    public FlinkCommandExecutor(JobContext jobContext) {
        super(jobContext);
        flinkData = JSONUtils.parseObject(jobContext.getJobInstance().getJob().getData(), FlinkData.class);
    }

    private FlinkData flinkData;

    @Override
    public String buildCommandFilePath() {
        return String.format("%s/%s/%s", jobContext.getExecutePath(), jobContext.getJobInstance().getInstanceId(),
                flinkData.getFileName());
    }

    @Override
    public String commandInterpreter() {
        return String.format("%s/flink-1.12.0/bin/%s", PropertyUtils.getString(Constants.FLINK_BASE_PATH), "flink");
    }

    @Override
    public List<String> commandArgs() {
        return Objects.requireNonNull(flinkData).buildArgs();
    }

    @Override
    public void buildCommandFile() throws IOException {
        HadoopUtils.getInstance().copyHdfsToLocal(flinkData.getUrl(), buildCommandFilePath(), false, false);
        //AliyunUtils.download(flinkData.getUrl().replace(AliyunUtils.getLocationPrefix(), ""), buildCommandFilePath());
    }
}
