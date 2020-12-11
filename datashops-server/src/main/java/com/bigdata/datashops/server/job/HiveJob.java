package com.bigdata.datashops.server.job;

import org.slf4j.Logger;

public class HiveJob extends AbstractJob {
    private String connectParams;

    private String sqlParams;

    public HiveJob(JobContext jobContext, Logger logger) {
        super(jobContext, logger);
    }

    @Override
    public void handle() {

    }
}
