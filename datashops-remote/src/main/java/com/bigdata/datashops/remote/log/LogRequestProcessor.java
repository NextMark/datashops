package com.bigdata.datashops.remote.log;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bigdata.datashops.common.Constants;
import com.bigdata.datashops.common.utils.JSONUtils;
import com.bigdata.datashops.common.utils.NetUtils;
import com.bigdata.datashops.common.utils.PropertyUtils;
import com.bigdata.datashops.model.pojo.log.RollViewLogRequest;
import com.bigdata.datashops.model.pojo.rpc.Host;
import com.bigdata.datashops.protocol.GrpcRequest;
import com.bigdata.datashops.remote.rpc.GrpcRemotingClient;
import com.google.protobuf.ByteString;

@Service
public class LogRequestProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(LogRequestProcessor.class);

    @Autowired
    private GrpcRemotingClient grpcRemotingClient;

    public String rollReadLog(String filePath, int skipLines, int limit, Host host) {
        RollViewLogRequest rollViewLogRequest = new RollViewLogRequest();
        rollViewLogRequest.setLimit(limit);
        rollViewLogRequest.setPath(filePath);
        rollViewLogRequest.setSkipLineNum(skipLines);
        GrpcRequest.Request request = GrpcRequest.Request.newBuilder().setIp(NetUtils.getLocalAddress())
                                              .setPort(PropertyUtils.getInt(Constants.WORKER_GRPC_SERVER_PORT))
                                              .setRequestId(RandomStringUtils.randomNumeric(6))
                                              .setRequestType(GrpcRequest.RequestType.ROLL_READ_LOG_REQUEST).setBody(
                        ByteString.copyFrom(JSONUtils.toJsonString(rollViewLogRequest).getBytes())).build();
        GrpcRequest.Response response = grpcRemotingClient.send(request, host);
        LOG.info("roll read log response {} {} {}", response.getRequestId(), response.getRequestType(),
                response.getBody());
        if (response.getBody() != null) {
            return response.getBody().toStringUtf8();
        }
        return null;
    }
}
