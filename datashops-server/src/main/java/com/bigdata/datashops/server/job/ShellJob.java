package com.bigdata.datashops.server.job;

import java.io.IOException;
import java.util.Objects;

import com.bigdata.datashops.common.utils.JSONUtils;
import com.bigdata.datashops.model.pojo.job.data.ShellData;
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
        ShellData shellData = JSONUtils.parseObject(jobInstance.getData(), ShellData.class);
        return Objects.requireNonNull(shellData).getValue();
    }
}
