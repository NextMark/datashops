package com.bigdata.datashops.server.job;

import com.bigdata.datashops.server.job.executor.FlinkCommandExecutor;

public class FlinkPerJob extends AbstractYarnJob {

    private FlinkCommandExecutor flinkCommandExecutor;

    public FlinkPerJob(JobContext jobContext) {
        super(jobContext);
        flinkCommandExecutor = new FlinkCommandExecutor(jobContext);
    }

    @Override
    protected void process() {
        try {
            commandResult = flinkCommandExecutor.run();
            success();
        } catch (Exception e) {
            LOG.error(String.format("Job execute error class=%s, name=%s, instanceId=%s", this.getClass(),
                    jobInstance.getJob().getName(), jobInstance.getInstanceId()), e);
            fail();
        }
    }

    @Override
    protected void after() {
        success();
    }
}
