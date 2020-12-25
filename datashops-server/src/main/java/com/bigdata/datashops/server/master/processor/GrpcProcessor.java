package com.bigdata.datashops.server.master.processor;

import java.util.concurrent.ExecutorService;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bigdata.datashops.protocol.GrpcRequest;
import com.bigdata.datashops.server.executor.ThreadUtil;
import com.bigdata.datashops.server.master.config.MasterConfig;
import com.bigdata.datashops.server.worker.executor.JobExecutor;

@Component
public class GrpcProcessor implements InitializingBean {
    @Autowired
    private MasterConfig masterConfig;

    private ExecutorService executorService;

    @Override
    public void afterPropertiesSet() {
        this.executorService = ThreadUtil.newDaemonFixedThreadExecutor("", masterConfig.getMasterJobThreads());
    }

    public void process(GrpcRequest.Request request) {
        GrpcRequest.RequestType type = request.getRequestType();
        switch (type) {
            case JOB_EXECUTE_RESPONSE:
                Runnable thread = new JobExecutor(request);
                executorService.submit(thread);
                break;
            case HEART_BEAT:
                break;
            default:
                break;
        }
    }
    public void processJobResponse(GrpcRequest.Request request) {

    }

    public void processHeartBeat(GrpcRequest.Request request) {

    }
}
