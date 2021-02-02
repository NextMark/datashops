package com.bigdata.datashops.server.master;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bigdata.datashops.server.master.registry.MasterRegistry;
import com.bigdata.datashops.server.master.scheduler.Finder;
import com.bigdata.datashops.server.master.scheduler.ScheduledExecutor;
import com.bigdata.datashops.server.quartz.QuartzService;
import com.bigdata.datashops.server.rpc.GrpcRemotingServer;
import com.bigdata.datashops.service.JobInstanceService;

@Component
public class MasterServer {
    @Autowired
    private QuartzService quartzService;

    @Autowired
    private MasterRegistry masterRegistry;

    @Autowired
    private GrpcRemotingServer grpcRemotingServer;

    @Autowired
    private ScheduledExecutor scheduledExecutor;

    @Autowired
    private JobInstanceService jobInstanceService;

    //    public static void main(String[] args) {
    //        Thread.currentThread().setName("Master Server");
    //        new SpringApplicationBuilder(MasterServer.class).web(WebApplicationType.NONE).run(args);
    //    }

    @PostConstruct
    public void init() throws IOException, InterruptedException {
        masterRegistry.registry();

        //quartzService.start();
        scheduledExecutor.run(new Finder(jobInstanceService));

        grpcRemotingServer.start();
        grpcRemotingServer.blockUntilShutdown();

        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
    }

    private void close() {
        quartzService.shutdown();
        masterRegistry.unRegistry();
    }

}
