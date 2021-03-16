package com.bigdata.datashops.server.worker.processor;

import java.util.concurrent.ExecutorService;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bigdata.datashops.common.Constants;
import com.bigdata.datashops.common.utils.PropertyUtils;
import com.bigdata.datashops.protocol.GrpcRequest;
import com.bigdata.datashops.server.job.JobManager;
import com.bigdata.datashops.server.thread.ThreadUtil;
import com.bigdata.datashops.server.worker.executor.JobExecutor;

@Component
public class WorkerGrpcProcessor implements InitializingBean {

    private ExecutorService executorService;

    @Autowired
    JobManager jobManager;

    @Override
    public void afterPropertiesSet() {
        this.executorService = ThreadUtil.newDaemonFixedThreadExecutor("worker-job-executor",
                PropertyUtils.getInt(Constants.WORKER_JOB_EXEC_THREADS));
    }

    public void processJobExec(GrpcRequest.Request request) {
        Runnable thread = new JobExecutor(request, jobManager);
        executorService.submit(thread);
    }

}
