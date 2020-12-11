package com.bigdata.datashops.server.executor;

import org.slf4j.Logger;

import com.bigdata.datashops.server.job.AbstractJob;
import com.bigdata.datashops.server.job.JobContext;
import com.bigdata.datashops.server.job.JobManager;

public class JobExecutor implements Runnable {

    private JobContext jobContext;

    private Logger logger;

    private AbstractJob job;

    public JobExecutor(JobContext jobContext, Logger logger) {
        this.jobContext = jobContext;
        this.logger = logger;
    }

    @Override
    public void run() {
        job = JobManager.createJob(jobContext, logger);
        job.execute();
    }
}
