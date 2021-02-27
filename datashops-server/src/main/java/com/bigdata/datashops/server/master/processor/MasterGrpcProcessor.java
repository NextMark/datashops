package com.bigdata.datashops.server.master.processor;

import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bigdata.datashops.protocol.GrpcRequest;
import com.bigdata.datashops.server.config.BaseConfig;
import com.bigdata.datashops.server.executor.ThreadUtil;
import com.bigdata.datashops.server.job.JobManager;
import com.bigdata.datashops.server.worker.executor.JobExecutor;

@Component
public class MasterGrpcProcessor implements InitializingBean {
    private static final Logger LOG = LoggerFactory.getLogger(MasterGrpcProcessor.class);
    @Autowired
    private BaseConfig baseConfig;

    private ExecutorService executorService;

    @Autowired
    private JobManager jobManager;

    @Override
    public void afterPropertiesSet() {
        this.executorService = ThreadUtil.newDaemonFixedThreadExecutor("", baseConfig.getMasterJobThreads());
    }

    public void process(GrpcRequest.Request request) {
        GrpcRequest.RequestType type = request.getRequestType();
        switch (type) {
            case JOB_EXECUTE_RESPONSE:
                Runnable thread = new JobExecutor(request, jobManager);
                executorService.submit(thread);
                break;
            case HEART_BEAT:
                break;
            default:
                break;
        }
    }

    public void processJobResponse(GrpcRequest.Request request) {
        LOG.info("Receive worker result {}", request.getBody().toStringUtf8());
    }

    public void processHeartBeat(GrpcRequest.Request request) {

    }
}
