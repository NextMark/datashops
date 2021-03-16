package com.bigdata.datashops.server.job;

import com.bigdata.datashops.common.Constants;
import com.bigdata.datashops.common.utils.JSONUtils;
import com.bigdata.datashops.common.utils.PropertyUtils;
import com.bigdata.datashops.model.pojo.job.data.FlinkData;
import com.bigdata.datashops.server.job.excutor.FlinkCommandExecutor;

public class FlinkJob extends AbstractJob {

    private FlinkCommandExecutor flinkCommandExecutor;

    public FlinkJob(JobContext jobContext) {
        super(jobContext);
        flinkCommandExecutor = new FlinkCommandExecutor(jobContext);
    }

    @Override
    protected void process() throws Exception {
        CommandResult commandResult = flinkCommandExecutor.run(buildCommand());
        buildGrpcRequest(commandResult);
    }

    private String buildCommand() {
        FlinkData flinkData = JSONUtils.parseObject(jobInstance.getData(), FlinkData.class);
        String flinkPath = PropertyUtils.getString(Constants.FLINK_BASE_PATH);
        String localJar = "";
        String command = String.format(
                "%s/flink-%s/bin/%s run -m yarn-cluster -d -ynm %s -yq %s -yjm %s -ytm %s -yn %s -ys %s -c %s -p %s "
                        + "-j %s %s", flinkPath, flinkData.getVersion(), flinkCommandExecutor.commandInterpreter(),
                flinkData.getYnm(), flinkData.getYq(), flinkData.getYjm(), flinkData.getYtm(), flinkData.getYn(),
                flinkData.getYs(), flinkData.getC(), flinkData.getP(), localJar, flinkData.getExtension());
        return command;
    }
}
