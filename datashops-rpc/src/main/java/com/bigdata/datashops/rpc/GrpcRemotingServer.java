package com.bigdata.datashops.rpc;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bigdata.datashops.protocol.GrpcRequest;
import com.bigdata.datashops.protocol.RequestServiceGrpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

public class GrpcRemotingServer {
    private final Logger LOG = LoggerFactory.getLogger(GrpcRemotingServer.class);

    private Server server;

    private GrpcServerConfig grpcServerConfig;

    public GrpcRemotingServer(GrpcServerConfig grpcServerConfig) {
        this.grpcServerConfig = grpcServerConfig;
    }

    public void start() throws IOException {
        LOG.info("Grpc server starting...");
        RequestServiceGrpcImpl requestServiceGrpc = new RequestServiceGrpcImpl();
        server = ServerBuilder.forPort(grpcServerConfig.getPort()).addService(requestServiceGrpc).build().start();
        LOG.info("Grpc server started, listening on {}", grpcServerConfig.getPort());
        Runtime.getRuntime().addShutdownHook(new Thread(GrpcRemotingServer.this::stop));
    }

    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    private void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    public class RequestServiceGrpcImpl extends RequestServiceGrpc.RequestServiceImplBase {
        @Override
        public void send(GrpcRequest.Request request, StreamObserver<GrpcRequest.Response> responseObserver) {
            LOG.info("[Grpc] receive request, rid {}, host {}, type {}", request.getRequestId(), request.getHost(),
                    request.getRequestType());
            // TODO
            GrpcRequest.Response response =
                    GrpcRequest.Response.newBuilder().setHost(request.getHost() + " aa").build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

}
