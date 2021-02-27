package com.bigdata.datashops.server.worker.processor;

import java.util.concurrent.ExecutorService;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bigdata.datashops.protocol.GrpcRequest;
import com.bigdata.datashops.server.config.BaseConfig;
import com.bigdata.datashops.server.executor.ThreadUtil;
import com.bigdata.datashops.server.job.JobManager;
import com.bigdata.datashops.server.worker.executor.JobExecutor;

@Component
public class WorkerGrpcProcessor implements InitializingBean {
    @Autowired
    private BaseConfig baseConfig;

    private ExecutorService executorService;

    @Autowired
    JobManager jobManager;

    @Override
    public void afterPropertiesSet() {
        this.executorService = ThreadUtil.newDaemonFixedThreadExecutor("", baseConfig.getWorkerJobThreads());
    }

    public void processJobExec(GrpcRequest.Request request) {
        Runnable thread = new JobExecutor(request, jobManager);
        executorService.submit(thread);
    }

}
