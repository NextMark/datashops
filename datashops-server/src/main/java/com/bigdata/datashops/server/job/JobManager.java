package com.bigdata.datashops.server.job;

import org.slf4j.Logger;

import com.bigdata.datashops.model.enums.JobType;

public class JobManager {
    public static AbstractJob createJob(JobContext jobContext, Logger logger) {
        JobType jobType = JobType.of(jobContext.getJobType());
        switch (jobType) {
            case HIVE:
                return new HiveJob(jobContext, logger);
            case SHELL:
            case SPARK:
            case FLINK:
            default:
                throw new IllegalArgumentException();
        }
    }
}
