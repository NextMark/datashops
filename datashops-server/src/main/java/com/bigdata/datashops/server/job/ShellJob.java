package com.bigdata.datashops.server.job;

import java.io.IOException;

import com.bigdata.datashops.server.job.excutor.ShellCommandExecutor;

public class ShellJob extends AbstractJob {
    public ShellJob(JobContext jobContext) {
        super(jobContext);
        shellCommandExecutor = new ShellCommandExecutor(jobContext);
    }

    private ShellCommandExecutor shellCommandExecutor;

    @Override
    public void before() {
    }

    @Override
    public void process() throws IOException, InterruptedException {
        CommandResult commandResult = shellCommandExecutor.run(buildCommand());
        buildGrpcRequest(commandResult);
    }

    @Override
    public void after() {
        LOG.info("Shell job execute end");
        grpcRemotingClient.send(request, selectHost());
    }

    private String buildCommand() {
        return "#!/bin/bash\ntouch /Users/qinshiwei/1.txt";
    }
}
