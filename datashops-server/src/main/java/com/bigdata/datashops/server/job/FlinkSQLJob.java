package com.bigdata.datashops.server.job;

public class FlinkSQLJob extends AbstractYarnJob {
    public FlinkSQLJob(JobContext jobContext) {
        super(jobContext);
    }

    @Override
    public void before() {
    }

    @Override
    protected void process() {

    }

    @Override
    protected void after() {
        success();
    }
}
