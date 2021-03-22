package com.bigdata.datashops.server.job.excutor;

import java.util.List;

import com.bigdata.datashops.server.job.JobContext;

public class ShellCommandExecutor extends CommandExecutor {
    public ShellCommandExecutor(JobContext jobContext) {
        super(jobContext);
    }

    @Override
    public String buildCommandFilePath() {
        return String.format("%s/%s.%s", jobContext.getExecutePath(), jobContext.getJobInstance().getInstanceId(),
                "sh");
    }

    @Override
    public String commandInterpreter() {
        return "bash";
    }

    @Override
    public List<String> commandArgs() {
        return null;
    }

}
