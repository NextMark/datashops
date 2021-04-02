package com.bigdata.datashops.server.job;

import com.bigdata.datashops.server.job.excutor.FlinkCommandExecutor;

public class FlinkPerJob extends AbstractJob {

    private FlinkCommandExecutor flinkCommandExecutor;

    public FlinkPerJob(JobContext jobContext) {
        super(jobContext);
        flinkCommandExecutor = new FlinkCommandExecutor(jobContext);
    }

    @Override
    protected void process() throws Exception {
        try {
            commandResult = flinkCommandExecutor.run();
            success();
        } catch (Exception e) {
            fail();
        }
    }

    @Override
    protected void after() {
        success();
    }
}
