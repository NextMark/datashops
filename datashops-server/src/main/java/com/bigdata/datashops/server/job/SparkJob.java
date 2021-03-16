package com.bigdata.datashops.server.job;

import com.bigdata.datashops.common.utils.JSONUtils;
import com.bigdata.datashops.model.pojo.job.data.SparkData;
import com.bigdata.datashops.server.job.excutor.SparkCommandExecutor;

public class SparkJob extends AbstractJob {
    private SparkCommandExecutor sparkCommandExecutor;

    public SparkJob(JobContext jobContext) {
        super(jobContext);
        sparkCommandExecutor = new SparkCommandExecutor(jobContext);
    }

    @Override
    protected void process() throws Exception {
        CommandResult commandResult = sparkCommandExecutor.run(buildCommand());
        buildGrpcRequest(commandResult);
    }

    private String buildCommand() {
        SparkData sparkData = JSONUtils.parseObject(jobInstance.getData(), SparkData.class);
        String localJar = "";
        return String.format(
                "%s --master yarn --deploy-mode cluster --class %s --driver-memory %s --executor-memory %s "
                        + "--executor-core %s --queue %s %s %s",
                sparkCommandExecutor.commandInterpreter(), sparkData.getClassName(), sparkData.getDriverMemory(),
                sparkData.getExecutorMemory(), sparkData.getExecutorCore(), sparkData.getQueue(),
                sparkData.getExtension(), localJar);
    }
}
