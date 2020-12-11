package com.bigdata.datashops.server.zookeeper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:zookeeper.properties")
public class ZookeeperConfig {

    //zk connect config
    @Value("${zookeeper.quorum}")
    private String serverList;

    @Value("${zookeeper.retry.base.sleep:100}")
    private int baseSleepTimeMs;

    @Value("${zookeeper.retry.max.sleep:30000}")
    private int maxSleepMs;

    @Value("${zookeeper.retry.maxtime:10}")
    private int maxRetries;

    @Value("${zookeeper.session.timeout:60000}")
    private int sessionTimeoutMs;

    @Value("${zookeeper.connection.timeout:30000}")
    private int connectionTimeoutMs;

    @Value("${zookeeper.connection.digest: }")
    private String digest;

    @Value("${zookeeper.max.wait.time:10000}")
    private int maxWaitTime;

    public String getServerList() {
        return serverList;
    }

    public void setServerList(String serverList) {
        this.serverList = serverList;
    }

    public int getBaseSleepTimeMs() {
        return baseSleepTimeMs;
    }

    public void setBaseSleepTimeMs(int baseSleepTimeMs) {
        this.baseSleepTimeMs = baseSleepTimeMs;
    }

    public int getMaxSleepMs() {
        return maxSleepMs;
    }

    public void setMaxSleepMs(int maxSleepMs) {
        this.maxSleepMs = maxSleepMs;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public int getSessionTimeoutMs() {
        return sessionTimeoutMs;
    }

    public void setSessionTimeoutMs(int sessionTimeoutMs) {
        this.sessionTimeoutMs = sessionTimeoutMs;
    }

    public int getConnectionTimeoutMs() {
        return connectionTimeoutMs;
    }

    public void setConnectionTimeoutMs(int connectionTimeoutMs) {
        this.connectionTimeoutMs = connectionTimeoutMs;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public int getMaxWaitTime() {
        return maxWaitTime;
    }

    public void setMaxWaitTime(int maxWaitTime) {
        this.maxWaitTime = maxWaitTime;
    }
}
