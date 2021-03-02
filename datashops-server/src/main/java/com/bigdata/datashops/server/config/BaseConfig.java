package com.bigdata.datashops.server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
@PropertySource("classpath:datashops.properties")
public class BaseConfig {
    @Value("${master.rpc.process.threads:20}")
    private int masterJobThreads;

    @Value("${master.finder.interval:3}")
    private int masterFinderInterval;

    @Value("${worker.job.exec.threads:100}")
    private int workerJobThreads;

    @Value("${master.heartbeat.interval:3}")
    private int masterHeartbeatInterval;

    @Value("${worker.heartbeat.interval:3}")
    private int workerHeartbeatInterval;

    @Value("${master.grpc.server.port:60000}")
    private int masterPort;

    @Value("${worker.grpc.server.port:60000}")
    private int workerPort;
}
