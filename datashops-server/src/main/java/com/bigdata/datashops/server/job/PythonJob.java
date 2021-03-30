package com.bigdata.datashops.server.job;

import com.bigdata.datashops.server.job.excutor.PythonCommandExecutor;

public class PythonJob extends AbstractJob {
    public PythonJob(JobContext jobContext) {
        super(jobContext);
        pythonCommandExecutor = new PythonCommandExecutor(jobContext);
    }

    private PythonCommandExecutor pythonCommandExecutor;

    @Override
    protected void process() throws Exception {
        commandResult = pythonCommandExecutor.run();
    }

    @Override
    protected void after() {
        LOG.info("Job end");
        success();
    }
}
