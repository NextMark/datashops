package com.bigdata.datashops.server.master;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.bigdata.datashops.common.Constants;
import com.bigdata.datashops.common.utils.PropertyUtils;
import com.bigdata.datashops.server.master.heartbeat.MasterHeartBeat;
import com.bigdata.datashops.server.master.registry.MasterRegistry;
import com.bigdata.datashops.server.master.scheduler.Finder;
import com.bigdata.datashops.server.queue.JobQueue;
import com.bigdata.datashops.server.rpc.GrpcRemotingServer;
import com.bigdata.datashops.server.rpc.MasterRequestServiceGrpcImpl;
import com.bigdata.datashops.server.thread.ThreadUtil;
import com.bigdata.datashops.service.JobInstanceService;

@ComponentScan(basePackages = {"com.bigdata.datashops"})
@EnableJpaRepositories(basePackages = {"com.bigdata.datashops.dao"})
@EntityScan("com.bigdata.datashops")
@EnableScheduling
@EnableTransactionManagement
@SpringBootApplication
public class MasterServer {
    private static final Logger LOG = LoggerFactory.getLogger(MasterServer.class);

    @Autowired
    private MasterRegistry masterRegistry;

    @Autowired
    private GrpcRemotingServer grpcRemotingServer;

    @Autowired
    private JobInstanceService jobInstanceService;

    @Qualifier("schedulerFactoryBean")
    @Autowired
    private Scheduler scheduler;

    @Autowired
    private Finder finder;

    @Autowired
    private JobQueue jobQueue;

    @Autowired
    private MasterHeartBeat heartBeat;

    @Autowired
    MasterRequestServiceGrpcImpl requestServiceGrpc;

    public static void main(String[] args) {
        Thread.currentThread().setName("Master Server");
        new SpringApplicationBuilder(MasterServer.class).web(WebApplicationType.NONE).run(args);
    }

    @PostConstruct
    public void init() throws IOException, InterruptedException, SchedulerException {
        masterRegistry.registry();
        ThreadUtil.scheduleAtFixedRate(heartBeat, PropertyUtils.getInt(Constants.MASTER_HEARTBEAT_INTERVAL));

        // 启动quartz任务
        scheduler.start();

        // TODO 不知道原因，需要先调用一下，负责线程中无法save
        jobInstanceService.findById(0);

        // 初始化zk队列
        jobQueue.initQueue();

        // 扫描作业实例
        ThreadUtil.scheduleAtFixedRate(finder, PropertyUtils.getInt(Constants.MASTER_FINDER_INTERVAL));

        grpcRemotingServer.start(PropertyUtils.getInt(Constants.MASTER_GRPC_SERVER_PORT), requestServiceGrpc);
        grpcRemotingServer.blockUntilShutdown();

        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
    }

    private void close() {
        if (scheduler != null) {
            try {
                scheduler.shutdown();
                LOG.info("shutdown quartz");
            } catch (SchedulerException e) {
            }
        }
        masterRegistry.unRegistry();
    }

}
