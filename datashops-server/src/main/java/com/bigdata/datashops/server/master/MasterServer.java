package com.bigdata.datashops.server.master;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;

import com.bigdata.datashops.server.master.registry.MasterRegistry;
import com.bigdata.datashops.server.master.scheduler.Finder;
import com.bigdata.datashops.server.master.scheduler.ScheduledExecutor;
import com.bigdata.datashops.server.quartz.QuartzService;
import com.bigdata.datashops.server.rpc.GrpcRemotingServer;

@ComponentScan(value = {"com.bigdata.datashops.server"})
public class MasterServer {
    @Autowired
    private QuartzService quartzService;

    @Autowired
    private MasterRegistry masterRegistry;

    @Autowired
    private GrpcRemotingServer grpcRemotingServer;

    @Autowired
    private ScheduledExecutor scheduledExecutor;

    public static void main(String[] args) {
        Thread.currentThread().setName("master startup");
        new SpringApplicationBuilder(MasterServer.class).web(WebApplicationType.NONE).run(args);
    }

    @PostConstruct
    public void run() throws IOException, InterruptedException {
        masterRegistry.registry();

        //quartzService.start();
        scheduledExecutor.run(new Finder());

        grpcRemotingServer.start();
        grpcRemotingServer.blockUntilShutdown();

        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
    }

    private void close() {
        quartzService.shutdown();
        masterRegistry.unRegistry();
    }

}
