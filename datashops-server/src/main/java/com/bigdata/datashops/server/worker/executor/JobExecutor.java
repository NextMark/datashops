package com.bigdata.datashops.server.worker.executor;

import org.slf4j.Logger;

import com.bigdata.datashops.common.utils.FileUtils;
import com.bigdata.datashops.common.utils.JSONUtils;
import com.bigdata.datashops.model.pojo.job.JobInstance;
import com.bigdata.datashops.protocol.GrpcRequest;
import com.bigdata.datashops.server.job.AbstractJob;
import com.bigdata.datashops.server.job.JobManager;
import com.bigdata.datashops.server.utils.LogUtils;

public class JobExecutor implements Runnable {
    private AbstractJob job;

    private GrpcRequest.Request request;

    JobManager jobManager;

    public JobExecutor(GrpcRequest.Request request, JobManager jobManager) {
        this.request = request;
        this.jobManager = jobManager;
    }

    @Override
    public void run() {
        String body = request.getBody().toStringUtf8();
        JobInstance instance = JSONUtils.parseObject(body, JobInstance.class);
        Logger logger = LogUtils.getLogger("%d{yyyyMMddHH}-" + instance.getInstanceId() + ".log", "job",
                FileUtils.getJobExecLogDir());
        logger.info("Run job from {}, id: {}", request.getHost(), request.getRequestId());
        job = jobManager.createJob(instance, logger);
        try {
            job.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
