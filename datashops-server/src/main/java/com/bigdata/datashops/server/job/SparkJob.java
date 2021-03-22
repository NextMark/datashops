package com.bigdata.datashops.server.job;

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
        return null;
    }

}
