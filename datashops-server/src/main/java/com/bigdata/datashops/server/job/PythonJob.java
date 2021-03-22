package com.bigdata.datashops.server.job;

import java.util.Objects;

import com.bigdata.datashops.common.utils.JSONUtils;
import com.bigdata.datashops.model.pojo.job.data.PythonData;
import com.bigdata.datashops.server.job.excutor.PythonCommandExecutor;

public class PythonJob extends AbstractJob {
    public PythonJob(JobContext jobContext) {
        super(jobContext);
        pythonCommandExecutor = new PythonCommandExecutor(jobContext);
    }

    private PythonCommandExecutor pythonCommandExecutor;

    @Override
    protected void process() throws Exception {
        CommandResult commandResult = pythonCommandExecutor.run(buildCommand());
        buildGrpcRequest(commandResult);
    }

    @Override
    protected void after() {
        LOG.info("Job end");
        grpcRemotingClient.send(request, selectHost());
    }

    private String buildCommand() {
        PythonData pythonData = JSONUtils.parseObject(jobInstance.getData(), PythonData.class);
        return Objects.requireNonNull(pythonData).getValue();
    }
}
