package com.bigdata.datashops.server.master.processor;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bigdata.datashops.common.Constants;
import com.bigdata.datashops.common.utils.JSONUtils;
import com.bigdata.datashops.common.utils.PropertyUtils;
import com.bigdata.datashops.model.enums.RunState;
import com.bigdata.datashops.model.pojo.job.JobInstance;
import com.bigdata.datashops.protocol.GrpcRequest;
import com.bigdata.datashops.server.job.JobManager;
import com.bigdata.datashops.server.job.JobResult;
import com.bigdata.datashops.server.thread.ThreadUtil;
import com.bigdata.datashops.server.worker.executor.JobExecutor;
import com.bigdata.datashops.service.JobInstanceService;

@Component
public class MasterGrpcProcessor implements InitializingBean {
    private static final Logger LOG = LoggerFactory.getLogger(MasterGrpcProcessor.class);

    private ExecutorService executorService;

    @Autowired
    private JobManager jobManager;

    @Autowired
    private JobInstanceService jobInstanceService;

    @Override
    public void afterPropertiesSet() {
        this.executorService = ThreadUtil.newDaemonFixedThreadExecutor("master-rpc-process",
                PropertyUtils.getInt(Constants.MASTER_RPC_PROCESS_THREADS));
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
        String body = request.getBody().toStringUtf8();
        int code = request.getCode();
        JobResult result = JSONUtils.parseObject(body, JobResult.class);
        JobInstance instance = jobInstanceService.findJobInstance("instanceId=" + result.getInstanceId());
        LOG.info("Receive worker finish rpc [{}]", result.getInstanceId());
        if (code == Constants.RPC_JOB_SUCCESS) {
            instance.setEndTime(new Date());
            instance.setState(RunState.SUCCESS.getCode());
        }
        if (code == Constants.RPC_JOB_FAIL) {
            instance.setState(RunState.FAILURE.getCode());
        }
        if (!Objects.isNull(instance)) {
            LOG.info("Job [{}] finish, save it", instance.getInstanceId());
            jobInstanceService.saveEntity(instance);
        }
    }

    public void processHeartBeat(GrpcRequest.Request request) {

    }
}
