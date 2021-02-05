package com.bigdata.datashops.server.master;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.bigdata.datashops.server.master.registry.MasterRegistry;
import com.bigdata.datashops.server.master.scheduler.Finder;
import com.bigdata.datashops.server.master.scheduler.ScheduledExecutor;
import com.bigdata.datashops.server.quartz.QuartzService;
import com.bigdata.datashops.server.rpc.GrpcRemotingServer;
import com.bigdata.datashops.service.JobInstanceService;

@EnableTransactionManagement
@ComponentScan(basePackages = "com.bigdata.datashops")
@EnableJpaRepositories(basePackages = {"com.bigdata.datashops.dao"})
@EntityScan("com.bigdata.datashops")
@SpringBootApplication
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

    public static void main(String[] args) {
        Thread.currentThread().setName("Master Server");
        new SpringApplicationBuilder(MasterServer.class).web(WebApplicationType.NONE).run(args);
    }

    @PostConstruct
    public void init() throws IOException, InterruptedException {
        masterRegistry.registry();

        quartzService.start();
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
