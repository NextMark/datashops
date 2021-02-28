package com.bigdata.datashops.server.worker;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import com.bigdata.datashops.server.config.BaseConfig;
import com.bigdata.datashops.server.master.MasterServer;
import com.bigdata.datashops.server.master.processor.MasterGrpcProcessor;
import com.bigdata.datashops.server.master.scheduler.ScheduledExecutor;
import com.bigdata.datashops.server.rpc.GrpcRemotingServer;
import com.bigdata.datashops.server.rpc.WorkerRequestServiceGrpcImpl;
import com.bigdata.datashops.server.worker.processor.HeartBeat;
import com.bigdata.datashops.server.worker.registry.WorkerRegistry;

@ComponentScan(basePackages = "com.bigdata.datashops", excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {MasterServer.class,
                MasterGrpcProcessor.class})})
public class WorkerServer {
    private static final Logger LOG = LoggerFactory.getLogger(WorkerServer.class);

    @Autowired
    private GrpcRemotingServer grpcRemotingServer;

    @Autowired
    private WorkerRegistry workerRegistry;

    @Autowired
    private ScheduledExecutor scheduledExecutor;

    @Autowired
    private HeartBeat heartBeat;

    @Autowired
    private BaseConfig baseConfig;

    @Autowired
    WorkerRequestServiceGrpcImpl requestServiceGrpc;

    public static void main(String[] args) {
        Thread.currentThread().setName("Worker Server");
        new SpringApplicationBuilder(WorkerServer.class).web(WebApplicationType.NONE).run(args);
    }

    @PostConstruct
    public void init() throws IOException, InterruptedException {
        workerRegistry.registry();
        scheduledExecutor.run(heartBeat);
        grpcRemotingServer.start(baseConfig.getWorkerPort(), requestServiceGrpc);
        grpcRemotingServer.blockUntilShutdown();

        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
    }

    private void close() {
        workerRegistry.unRegistry();
    }
}
