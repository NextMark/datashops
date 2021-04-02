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
        try {
            commandResult = pythonCommandExecutor.run();
            success();
        } catch (Exception e) {
            fail();
        }
    }

    @Override
    protected void after() {
        LOG.info("Job end");
        success();
    }
}
