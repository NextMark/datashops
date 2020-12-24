package com.bigdata.datashops.server.worker.executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bigdata.datashops.common.utils.JSONUtils;
import com.bigdata.datashops.protocol.GrpcRequest;
import com.bigdata.datashops.server.job.AbstractJob;
import com.bigdata.datashops.server.job.JobContext;
import com.bigdata.datashops.server.job.JobManager;
import com.google.protobuf.ByteString;

public class JobExecutor implements Runnable {
    private AbstractJob job;

    private GrpcRequest.Request request;

    public JobExecutor(GrpcRequest.Request request) {
        this.request = request;
    }

    @Override
    public void run() {
        ByteString body = request.getBody();
        String bodyStr = body.toStringUtf8();
        JobContext context = JSONUtils.parseObject(bodyStr, JobContext.class);
        JobContext jobContext = JobContext.builder()
                                        .jobName(context.getJobName())
                                        .jobType(context.getJobInstance().getType())
                                        .build();
        Logger logger = LoggerFactory.getLogger("");
        job = JobManager.createJob(jobContext, logger);
        job.execute();
    }

}
