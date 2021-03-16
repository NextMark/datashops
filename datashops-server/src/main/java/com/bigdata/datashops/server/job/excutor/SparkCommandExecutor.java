package com.bigdata.datashops.server.job.excutor;

import com.bigdata.datashops.server.job.JobContext;

public class SparkCommandExecutor extends CommandExecutor {
    public SparkCommandExecutor(JobContext jobContext) {
        super(jobContext);
    }

    @Override
    public String buildCommandFilePath() {
        return null;
    }

    @Override
    public String commandInterpreter() {
        return "spark-submit";
    }
}
