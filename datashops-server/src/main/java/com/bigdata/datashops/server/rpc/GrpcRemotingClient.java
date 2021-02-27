package com.bigdata.datashops.server.rpc;

import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bigdata.datashops.model.pojo.rpc.Host;
import com.bigdata.datashops.protocol.GrpcRequest;
import com.bigdata.datashops.protocol.RequestServiceGrpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

@Service
public class GrpcRemotingClient {
    private final Logger LOG = LoggerFactory.getLogger(GrpcRemotingClient.class);

    private ConcurrentHashMap<Host, RequestServiceGrpc.RequestServiceBlockingStub> stubs = new ConcurrentHashMap<>(32);

    public GrpcRequest.Response send(GrpcRequest.Request request, Host host) {
        LOG.info("Grpc Send request id {}, host {}, type {}", request.getRequestId(), request.getHost(),
                request.getRequestType());
        final RequestServiceGrpc.RequestServiceBlockingStub stub = getStub(host);
        GrpcRequest.Response response;
        try {
            response = stub.send(request);
        } catch (StatusRuntimeException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
        return response;
    }

    private RequestServiceGrpc.RequestServiceBlockingStub getStub(Host host) {
        RequestServiceGrpc.RequestServiceBlockingStub stub = stubs.get(host);
        if (stub != null) {
            return stub;
        }
        return createStub(host);
    }

    private RequestServiceGrpc.RequestServiceBlockingStub createStub(Host host) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(host.getIp(), host.getPort()).usePlaintext().build();
        RequestServiceGrpc.RequestServiceBlockingStub blockingStub = RequestServiceGrpc.newBlockingStub(channel);
        stubs.put(host, blockingStub);
        return blockingStub;
    }

}
