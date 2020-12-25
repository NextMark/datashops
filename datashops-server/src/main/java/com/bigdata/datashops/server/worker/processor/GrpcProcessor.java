package com.bigdata.datashops.server.worker.processor;

import java.util.concurrent.ExecutorService;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bigdata.datashops.protocol.GrpcRequest;
import com.bigdata.datashops.server.executor.ThreadUtil;
import com.bigdata.datashops.server.worker.executor.JobExecutor;
import com.bigdata.datashops.server.worker.config.WorkerConfig;

@Component
public class GrpcProcessor implements InitializingBean {
    @Autowired
    private WorkerConfig workerConfig;

    private ExecutorService executorService;

    @Override
    public void afterPropertiesSet() {
        this.executorService = ThreadUtil.newDaemonFixedThreadExecutor("", workerConfig.getWorkerJobThreads());
    }

    public void processJobExec(GrpcRequest.Request request) {
        GrpcRequest.RequestType type = request.getRequestType();
        switch (type) {
            case JOB_EXECUTE_REQUEST:
                Runnable thread = new JobExecutor(request);
                executorService.submit(thread);
                break;
            case JOB_EXECUTE_RESPONSE:
                break;
            default:
                break;
        }
    }

}
