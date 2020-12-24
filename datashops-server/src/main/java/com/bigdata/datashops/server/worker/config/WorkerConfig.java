package com.bigdata.datashops.server.worker.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public class WorkerConfig {
    @Value("${worker.exec.threads:100}")
    private int workerJobThreads;

    @Value("${worker.heartbeat.interval:10}")
    private int workerHeartbeatInterval;
}
