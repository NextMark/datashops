package com.bigdata.datashops.server.worker;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import com.bigdata.datashops.server.master.MasterServer;
import com.bigdata.datashops.server.rpc.GrpcRemotingServer;
import com.bigdata.datashops.server.worker.registry.WorkerRegistry;

@ComponentScan(basePackages = "com.bigdata.datashops", excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {MasterServer.class})})
public class WorkerServer {
    @Autowired
    private GrpcRemotingServer grpcRemotingServer;

    @Autowired
    private WorkerRegistry workerRegistry;

    public static void main(String[] args) {
        Thread.currentThread().setName("Worker Server");
        new SpringApplicationBuilder(WorkerServer.class).web(WebApplicationType.NONE).run(args);
    }

    @PostConstruct
    public void init() throws IOException, InterruptedException {
        workerRegistry.registry();

        grpcRemotingServer.start();
        grpcRemotingServer.blockUntilShutdown();

        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
    }

    private void close() {
        workerRegistry.unRegistry();
    }
}
