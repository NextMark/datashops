package com.bigdata.datashops.server.master;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.bigdata.datashops.rpc.GrpcRemotingServer;
import com.bigdata.datashops.rpc.GrpcServerConfig;
import com.bigdata.datashops.server.quartz.QuartzService;

public class MasterServer {
    @Autowired
    private QuartzService quartzService;

    @Autowired
    private MasterRegistry masterRegistry;

    @PostConstruct
    public void run() throws IOException, InterruptedException {
        masterRegistry.registry();

        quartzService.start();

        GrpcRemotingServer grpcRemotingServer = new GrpcRemotingServer(new GrpcServerConfig());
        grpcRemotingServer.start();
        grpcRemotingServer.blockUntilShutdown();

        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
    }

    private void close() {
        quartzService.shutdown();
        masterRegistry.unRegistry();
    }

}
