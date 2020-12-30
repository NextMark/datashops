package com.bigdata.datashops.server.worker.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.bigdata.datashops.common.utils.NetUtils;
import com.bigdata.datashops.server.utils.ZKUtils;
import com.bigdata.datashops.server.zookeeper.ZookeeperOperator;

@Component
public class HeartBeat {
    @Autowired
    private ZookeeperOperator zookeeperOperator;

    @Scheduled(cron = "*/10 * * * * ?")
    public void reportHostInfo() {
        String address = NetUtils.getLocalAddress();
        // TODO 计算权重
        float weight = 0.2f;
        zookeeperOperator.persistEphemeral(ZKUtils.getWorkerMetaPath() + address, String.valueOf(weight));
    }
}
