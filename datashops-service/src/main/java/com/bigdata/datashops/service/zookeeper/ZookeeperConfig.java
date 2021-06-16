package com.bigdata.datashops.service.zookeeper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
//@PropertySource("classpath:zookeeper.properties")
public class ZookeeperConfig {

    @Value("${zookeeper.quorum}")
    private String serverList;

    @Value("${zookeeper.retry.base.sleep:100}")
    private int baseSleepTimeMs;

    @Value("${zookeeper.retry.max.sleep:30000}")
    private int maxSleepMs;

    @Value("${zookeeper.retry.max.time:10}")
    private int maxRetries;

    @Value("${zookeeper.session.timeout:1000}")
    private int sessionTimeoutMs;

    @Value("${zookeeper.connection.timeout:30000}")
    private int connectionTimeoutMs;

    @Value("${zookeeper.connection.digest}")
    private String digest;

    @Value("${zookeeper.max.wait.time:10000}")
    private int maxWaitTime;
}
