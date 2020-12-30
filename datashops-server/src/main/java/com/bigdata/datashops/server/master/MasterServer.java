package com.bigdata.datashops.server.master;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.bigdata.datashops.server.master.registry.MasterRegistry;
import com.bigdata.datashops.server.quartz.QuartzService;
import com.bigdata.datashops.server.rpc.GrpcRemotingServer;

public class MasterServer {
    @Autowired
    private QuartzService quartzService;

    @Autowired
    private MasterRegistry masterRegistry;

    @Autowired
    private GrpcRemotingServer grpcRemotingServer;

    @PostConstruct
    public void run() throws IOException, InterruptedException {
        masterRegistry.registry();

        quartzService.start();

        grpcRemotingServer.start();
        grpcRemotingServer.blockUntilShutdown();

        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
    }

    private void close() {
        quartzService.shutdown();
        masterRegistry.unRegistry();
    }

}
