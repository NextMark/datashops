package com.bigdata.datashops.server.job.excutor;

import java.util.List;

import com.bigdata.datashops.server.job.JobContext;

public class PythonCommandExecutor extends CommandExecutor {

    public PythonCommandExecutor(JobContext jobContext) {
        super(jobContext);
    }

    @Override
    public String buildCommandFilePath() {
        return String.format("%s/%s.%s", jobContext.getExecutePath(), jobContext.getJobInstance().getInstanceId(),
                "py");
    }

    @Override
    public String commandInterpreter() {
        return "python";
    }

    @Override
    public List<String> commandArgs() {
        return null;
    }
}
