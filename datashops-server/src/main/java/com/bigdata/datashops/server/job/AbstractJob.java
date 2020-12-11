package com.bigdata.datashops.server.job;

import org.slf4j.Logger;

public abstract class AbstractJob {
    protected Logger logger;

    protected JobContext jobContext;

    public AbstractJob() {}

    public AbstractJob(JobContext jobContext, Logger logger) {
        this.jobContext = jobContext;
        this.logger = logger;
    }

    public void execute() {
        init();
        handle();
        after();
    };

    protected void init() {

    }

    protected abstract void handle();


    protected void after() {}
}
