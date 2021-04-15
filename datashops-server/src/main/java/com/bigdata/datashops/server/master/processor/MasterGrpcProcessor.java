package com.bigdata.datashops.server.master.processor;

import java.util.Collections;
import java.util.Date;
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
import com.bigdata.datashops.server.job.JobResult;
import com.bigdata.datashops.server.thread.ThreadUtil;
import com.bigdata.datashops.service.JobInstanceService;

@Component
public class MasterGrpcProcessor implements InitializingBean {
    private static final Logger LOG = LoggerFactory.getLogger(MasterGrpcProcessor.class);

    private ExecutorService executorService;

    @Autowired
    private JobInstanceService jobInstanceService;

    @Override
    public void afterPropertiesSet() {
        this.executorService = ThreadUtil.newDaemonFixedThreadExecutor("master-rpc-process",
                PropertyUtils.getInt(Constants.MASTER_RPC_PROCESS_THREADS));
    }

    public void processJobResponse(GrpcRequest.Request request) {
        String body = request.getBody().toStringUtf8();
        int code = request.getCode();
        JobResult result = JSONUtils.parseObject(body, JobResult.class);
        JobInstance instance = jobInstanceService.findJobInstance("instanceId=" + result.getInstanceId());
        jobInstanceService.fillJob(Collections.singletonList(instance));
        LOG.info("Receive worker finish rpc code={}, name={}, instanceId={}", code, instance.getJob().getName(),
                result.getInstanceId());

        if (code == Constants.RPC_JOB_APP_ID) {
            instance.setAppId(result.getData());
        }
        if (code == Constants.RPC_JOB_SUCCESS) {
            instance.setEndTime(new Date());
            instance.setState(RunState.SUCCESS.getCode());
        }
        if (code == Constants.RPC_JOB_FAIL) {
            instance.setEndTime(new Date());
            instance.setState(RunState.FAIL.getCode());
        }
        if (code == Constants.RPC_JOB_TIMEOUT_FAIL) {
            instance.setEndTime(new Date());
            instance.setState(RunState.TIMEOUT_FAIL.getCode());
        }
        LOG.info("Job name={}, instanceId={} finish, update it", instance.getJob().getName(), instance.getInstanceId());
        jobInstanceService.saveEntity(instance);
    }

    public void processJobKill(GrpcRequest.Request request) {
        String body = request.getBody().toStringUtf8();
        int code = request.getCode();
        JobResult result = JSONUtils.parseObject(body, JobResult.class);
        JobInstance instance = jobInstanceService.findJobInstance("instanceId=" + result.getInstanceId());
        jobInstanceService.fillJob(Collections.singletonList(instance));
        LOG.info("Receive job kill response, name={}, instanceId={}", instance.getJob().getName(),
                result.getInstanceId());
        if (code == Constants.RPC_JOB_SUCCESS) {
            instance.setEndTime(new Date());
            instance.setState(RunState.KILL.getCode());
        }
        if (code == Constants.RPC_JOB_FAIL) {
            instance.setState(RunState.FAIL.getCode());
        }
        LOG.info("Job killed, name={}, instanceId={} update it", instance.getJob().getName(), instance.getInstanceId());
        jobInstanceService.saveEntity(instance);
    }

    public void processHeartBeat(GrpcRequest.Request request) {

    }

}
