package com.bigdata.datashops.server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
@PropertySource("classpath:datashops.properties")
public class BaseConfig {
    @Value("${master.exec.threads:100}")
    private int masterJobThreads;

    @Value("${worker.exec.threads:100}")
    private int workerJobThreads;

    @Value("${worker.heartbeat.interval:10}")
    private int workerHeartbeatInterval;

    @Value("${master.grpc.server.port:60000}")
    private int masterPort;

    @Value("${worker.grpc.server.port:60000}")
    private int workerPort;
}
