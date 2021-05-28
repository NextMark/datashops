package com.bigdata.datashops.server.job;

import com.bigdata.datashops.server.job.executor.SparkCommandExecutor;

public class SparkJob extends AbstractYarnJob {
    private SparkCommandExecutor sparkCommandExecutor;

    public SparkJob(JobContext jobContext) {
        super(jobContext);
        sparkCommandExecutor = new SparkCommandExecutor(jobContext);
    }

    @Override
    protected void process() {
        try {
            commandResult = sparkCommandExecutor.run();
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
