package com.bigdata.datashops.server.job;

import com.bigdata.datashops.server.job.executor.PythonCommandExecutor;

public class PythonJob extends AbstractJob {
    public PythonJob(JobContext jobContext) {
        super(jobContext);
        pythonCommandExecutor = new PythonCommandExecutor(jobContext);
    }

    private PythonCommandExecutor pythonCommandExecutor;

    @Override
    protected void process() {
        try {
            commandResult = pythonCommandExecutor.run();
        } catch (Exception e) {
            LOG.error(String.format("Job execute error class=%s, name=%s, instanceId=%s", this.getClass(),
                    jobInstance.getJob().getName(), jobInstance.getInstanceId()), e);
            fail();
        }
    }

    @Override
    protected void after() {
        commandRpc();
    }
}
