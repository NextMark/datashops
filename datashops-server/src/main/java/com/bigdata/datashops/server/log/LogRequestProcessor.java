package com.bigdata.datashops.server.log;

import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bigdata.datashops.common.Constants;
import com.bigdata.datashops.common.utils.JSONUtils;
import com.bigdata.datashops.common.utils.NetUtils;
import com.bigdata.datashops.common.utils.PropertyUtils;
import com.bigdata.datashops.model.pojo.rpc.Host;
import com.bigdata.datashops.protocol.GrpcRequest;
import com.bigdata.datashops.server.rpc.GrpcRemotingClient;
import com.google.protobuf.ByteString;

@Service
public class LogRequestProcessor {
    @Autowired
    private GrpcRemotingClient grpcRemotingClient;

    public String rollReadLog(String filePath, int skipLineNum, int limit, Host host) {
        RollViewLogRequest rollViewLogRequest = new RollViewLogRequest();
        rollViewLogRequest.setLimit(limit);
        rollViewLogRequest.setPath(filePath);
        rollViewLogRequest.setSkipLineNum(skipLineNum);
        GrpcRequest.Request request = GrpcRequest.Request.newBuilder().setIp(NetUtils.getLocalAddress())
                                              .setPort(PropertyUtils.getInt(Constants.WORKER_GRPC_SERVER_PORT))
                                              .setRequestId(RandomUtils.nextInt())
                                              .setRequestType(GrpcRequest.RequestType.ROLL_READ_LOG_REQUEST).setBody(
                        ByteString.copyFrom(JSONUtils.toJsonString(rollViewLogRequest).getBytes())).build();
        GrpcRequest.Response response = grpcRemotingClient.send(request, host);
        return response.getBody().toStringUtf8();
    }
}
