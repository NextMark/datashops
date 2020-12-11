package com.bigdata.datashops.server.zookeeper;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.ACLProvider;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CuratorZookeeperClient implements InitializingBean {
    private final Logger LOG = LoggerFactory.getLogger(CuratorZookeeperClient.class);

    @Autowired
    private ZookeeperConfig zookeeperConfig;

    private CuratorFramework zkClient;

    @Override
    public void afterPropertiesSet() {
        this.zkClient = buildClient();
        initStateLister();
    }

    private CuratorFramework buildClient() {
        LOG.info("zookeeper registry center init, server lists is: {}.", zookeeperConfig.getServerList());

        CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder().ensembleProvider(
                new DefaultEnsembleProvider(zookeeperConfig.getServerList())).retryPolicy(
                new ExponentialBackoffRetry(zookeeperConfig.getBaseSleepTimeMs(), zookeeperConfig.getMaxRetries(),
                        zookeeperConfig.getMaxSleepMs()));

        //these has default value
        if (0 != zookeeperConfig.getSessionTimeoutMs()) {
            builder.sessionTimeoutMs(zookeeperConfig.getSessionTimeoutMs());
        }
        if (0 != zookeeperConfig.getConnectionTimeoutMs()) {
            builder.connectionTimeoutMs(zookeeperConfig.getConnectionTimeoutMs());
        }
        if (StringUtils.isNotBlank(zookeeperConfig.getDigest())) {
            builder.authorization("digest", zookeeperConfig.getDigest().getBytes(StandardCharsets.UTF_8))
                    .aclProvider(new ACLProvider() {

                        @Override
                        public List<ACL> getDefaultAcl() {
                            return ZooDefs.Ids.CREATOR_ALL_ACL;
                        }

                        @Override
                        public List<ACL> getAclForPath(final String path) {
                            return ZooDefs.Ids.CREATOR_ALL_ACL;
                        }
                    });
        }
        zkClient = builder.build();
        zkClient.start();
        try {
            zkClient.blockUntilConnected();
        } catch (final Exception ex) {
            throw new RuntimeException(ex);
        }
        return zkClient;
    }

    public void initStateLister() {
        zkClient.getConnectionStateListenable().addListener((client, newState) -> {
            if (newState == ConnectionState.LOST) {
                LOG.error("connection lost from zookeeper");
            } else if (newState == ConnectionState.RECONNECTED) {
                LOG.info("reconnected to zookeeper");
            } else if (newState == ConnectionState.SUSPENDED) {
                LOG.warn("connection SUSPENDED to zookeeper");
            }
        });
    }

    public ZookeeperConfig getZookeeperConfig() {
        return zookeeperConfig;
    }

    public void setZookeeperConfig(ZookeeperConfig zookeeperConfig) {
        this.zookeeperConfig = zookeeperConfig;
    }

    public CuratorFramework getZkClient() {
        return zkClient;
    }
}
