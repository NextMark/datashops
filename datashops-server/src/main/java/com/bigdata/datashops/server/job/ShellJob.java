package com.bigdata.datashops.server.job;

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
    public void process() {
        try {
            commandResult = shellCommandExecutor.run();
            success();
        } catch (Exception e) {
            LOG.error(String.format("Job execute error class=%s, name=%s, instanceId=%s", this.getClass(),
                    jobInstance.getName(), jobInstance.getInstanceId()), e);
            fail();
        }
    }

    @Override
    public void after() {
        LOG.info("Shell job execute end");
    }
}
