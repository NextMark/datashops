package com.bigdata.datashops.server;

import org.springframework.stereotype.Service;

import com.bigdata.datashops.model.pojo.rpc.Host;
import com.bigdata.datashops.protocol.GrpcRequest;
import com.bigdata.datashops.rpc.GrpcRemotingClient;

@Service
public class GrpcExecutorManager {
    private final GrpcRemotingClient grpcRemotingClient;

    public GrpcExecutorManager() {
        grpcRemotingClient = new GrpcRemotingClient();
    }

    public void execute(GrpcRequest.Request request, Host host) {
        GrpcRequest.Response response = grpcRemotingClient.send(request, host);
    }
}
