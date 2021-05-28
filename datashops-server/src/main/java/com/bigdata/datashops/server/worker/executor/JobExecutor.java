package com.bigdata.datashops.server.worker.executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bigdata.datashops.common.utils.FileUtils;
import com.bigdata.datashops.common.utils.JSONUtils;
import com.bigdata.datashops.model.pojo.job.JobInstance;
import com.bigdata.datashops.protocol.GrpcRequest;
import com.bigdata.datashops.server.job.AbstractJob;
import com.bigdata.datashops.server.job.JobManager;
import com.bigdata.datashops.server.utils.LogUtils;

public class JobExecutor implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(JobExecutor.class);

    private AbstractJob job;

    private GrpcRequest.Request request;

    private JobManager jobManager;

    public JobExecutor(GrpcRequest.Request request, JobManager jobManager) {
        this.request = request;
        this.jobManager = jobManager;
    }

    @Override
    public void run() {
        LOG.info("Run job from={}, id={}", request.getIp(), request.getRequestId());
        String body = request.getBody().toStringUtf8();
        JobInstance instance = JSONUtils.parseObject(body, JobInstance.class);
        Logger logger = LogUtils.getLogger("INFO", FileUtils.getJobExecLogDir(), instance.getInstanceId() + ".log",
                instance.getInstanceId() + ".log.%d{yyyyMMdd}.gz");
        logger.info("Run job from={}, id={}", request.getIp(), request.getRequestId());
        job = jobManager.createJob(instance, logger);
        try {
            job.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
