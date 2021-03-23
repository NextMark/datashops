package com.bigdata.datashops.server.job;

import com.bigdata.datashops.server.job.excutor.FlinkCommandExecutor;

public class FlinkJob extends AbstractJob {

    private FlinkCommandExecutor flinkCommandExecutor;

    public FlinkJob(JobContext jobContext) {
        super(jobContext);
        flinkCommandExecutor = new FlinkCommandExecutor(jobContext);
    }

    @Override
    protected void process() throws Exception {
        CommandResult commandResult = flinkCommandExecutor.run();
        buildGrpcRequest(commandResult);
    }
}
