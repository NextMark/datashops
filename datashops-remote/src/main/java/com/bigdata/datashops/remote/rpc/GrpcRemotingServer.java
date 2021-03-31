package com.bigdata.datashops.remote.rpc;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bigdata.datashops.protocol.RequestServiceGrpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;

@Service
public class GrpcRemotingServer {
    private final Logger LOG = LoggerFactory.getLogger(GrpcRemotingServer.class);

    private Server server;

    public void start(int port, RequestServiceGrpc.RequestServiceImplBase requestServiceImplBase) throws IOException {
        LOG.info("Grpc server starting...");
        server = ServerBuilder.forPort(port).addService(requestServiceImplBase).build().start();
        LOG.info("Grpc server started, listening on {}", port);
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

}
