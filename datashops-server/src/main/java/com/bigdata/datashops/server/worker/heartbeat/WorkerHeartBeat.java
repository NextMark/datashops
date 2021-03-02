package com.bigdata.datashops.server.worker.heartbeat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bigdata.datashops.common.utils.NetUtils;
import com.bigdata.datashops.server.config.BaseConfig;
import com.bigdata.datashops.server.utils.ZKUtils;
import com.bigdata.datashops.server.zookeeper.ZookeeperOperator;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class WorkerHeartBeat implements Runnable {
    @Autowired
    private ZookeeperOperator zookeeperOperator;

    @Autowired
    private BaseConfig baseConfig;

    @Override
    public void run() {
        String address = NetUtils.getLocalAddress();
        log.info("Report heartbeat {}", address);
        // TODO 计算权重
        float weight = 0.2f;
        zookeeperOperator.persistEphemeral(
                String.format("%s/%s_%s", ZKUtils.getWorkerRegistryPath(), address, baseConfig.getWorkerPort()),
                String.valueOf(weight));
    }
}
