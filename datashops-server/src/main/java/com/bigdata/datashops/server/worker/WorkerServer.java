package com.bigdata.datashops.server.worker;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.bigdata.datashops.server.rpc.GrpcRemotingServer;
import com.bigdata.datashops.server.worker.registry.WorkerRegistry;

public class WorkerServer {
    @Autowired
    private GrpcRemotingServer grpcRemotingServer;

    @Autowired
    private WorkerRegistry workerRegistry;

    @PostConstruct
    public void run() throws IOException, InterruptedException {
        workerRegistry.registry();

        grpcRemotingServer.start();
        grpcRemotingServer.blockUntilShutdown();

        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
    }

    private void close() {
        workerRegistry.unRegistry();
    }
}
