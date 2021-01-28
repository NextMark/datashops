package com.bigdata.datashops.server.job;

import org.slf4j.Logger;

import com.bigdata.datashops.model.enums.JobType;

public class JobManager {
    public static AbstractJob createJob(JobContext jobContext, Logger logger) {
        JobType jobType = JobType.of(jobContext.getJobInstance().getType());
        switch (jobType) {
            case HIVE:
                return new SqlJob(jobContext, logger);
            case BASH:
            case SPARK:
            case FLINK:
            default:
                throw new IllegalArgumentException();
        }
    }
}
