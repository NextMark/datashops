package com.bigdata.datashops.server.worker.processor;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bigdata.datashops.common.Constants;
import com.bigdata.datashops.common.utils.JSONUtils;
import com.bigdata.datashops.common.utils.NetUtils;
import com.bigdata.datashops.common.utils.PropertyUtils;
import com.bigdata.datashops.model.pojo.log.RollViewLogRequest;
import com.bigdata.datashops.protocol.GrpcRequest;
import com.bigdata.datashops.remote.rpc.GrpcRemotingClient;
import com.bigdata.datashops.server.job.JobManager;
import com.bigdata.datashops.server.thread.ThreadUtil;
import com.bigdata.datashops.server.worker.executor.JobExecutor;
import com.google.protobuf.ByteString;

@Component
public class WorkerGrpcProcessor implements InitializingBean {
    private static final Logger LOG = LoggerFactory.getLogger(WorkerGrpcProcessor.class);

    private ExecutorService executorService;

    @Autowired
    JobManager jobManager;

    @Autowired
    GrpcRemotingClient grpcRemotingClient;

    @Override
    public void afterPropertiesSet() {
        this.executorService = ThreadUtil.newDaemonFixedThreadExecutor("worker-job-executor",
                PropertyUtils.getInt(Constants.WORKER_JOB_EXEC_THREADS));
    }

    public void processJobExec(GrpcRequest.Request request) {
        Runnable thread = new JobExecutor(request, jobManager);
        executorService.submit(thread);
    }

    public GrpcRequest.Response rollReadLogRequest(GrpcRequest.Request request) {
        String body = request.getBody().toStringUtf8();
        RollViewLogRequest rollViewLogRequest = JSONUtils.parseObject(body, RollViewLogRequest.class);
        List<String> lines = readPartFileContent(rollViewLogRequest.getPath(), rollViewLogRequest.getSkipLineNum(),
                rollViewLogRequest.getLimit());
        StringBuilder builder = new StringBuilder();
        for (String line : lines) {
            builder.append(line + "\r\n");
        }
        return GrpcRequest.Response.newBuilder().setRequestId(request.getRequestId()).setCode(Constants.RPC_JOB_SUCCESS)
                       .setRequestType(GrpcRequest.RequestType.ROLL_READ_LOG_RESPONSE)
                       .setBody(ByteString.copyFrom(builder.toString().getBytes())).setIp(NetUtils.getLocalAddress())
                       .setPort(PropertyUtils.getInt(Constants.MASTER_GRPC_SERVER_PORT)).build();
    }

    public GrpcRequest.Response readWholeLogRequest(GrpcRequest.Request request) {
        String body = request.getBody().toStringUtf8();
        RollViewLogRequest rollViewLogRequest = JSONUtils.parseObject(body, RollViewLogRequest.class);
        String content = readWholeFileContent(rollViewLogRequest.getPath());

        return GrpcRequest.Response.newBuilder().setRequestId(request.getRequestId()).setCode(Constants.RPC_JOB_SUCCESS)
                       .setRequestType(GrpcRequest.RequestType.READ_WHOLE_LOG_RESPONSE)
                       .setBody(ByteString.copyFrom(content.getBytes())).setIp(NetUtils.getLocalAddress())
                       .setPort(PropertyUtils.getInt(Constants.MASTER_GRPC_SERVER_PORT)).build();
    }

    private List<String> readPartFileContent(String filePath, int skipLine, int limit) {
        try (Stream<String> stream = Files.lines(Paths.get(filePath))) {
            return stream.skip(skipLine).limit(limit).collect(Collectors.toList());
        } catch (IOException e) {
            LOG.error("read file error", e);
        }
        return Collections.emptyList();
    }

    private String readWholeFileContent(String filePath) {
        BufferedReader br = null;
        String line;
        StringBuilder sb = new StringBuilder();
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
            while ((line = br.readLine()) != null) {
                sb.append(line + "\r\n");
            }
            return sb.toString();
        } catch (IOException e) {
            LOG.error("read file error", e);
        } finally {
            IOUtils.closeQuietly(br);
        }
        return "";
    }

}
