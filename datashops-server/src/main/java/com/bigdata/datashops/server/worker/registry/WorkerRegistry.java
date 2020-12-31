package com.bigdata.datashops.server.worker.registry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bigdata.datashops.common.utils.NetUtils;
import com.bigdata.datashops.server.utils.ZKUtils;
import com.bigdata.datashops.server.zookeeper.ZookeeperOperator;

@Service
public class WorkerRegistry {
    @Autowired
    private ZookeeperOperator zookeeperOperator;

    public void registry() {
        String address = NetUtils.getLocalAddress();
        zookeeperOperator.persistEphemeral(ZKUtils.getWorkerRegistryPath() + address, "");
    }

    public void unRegistry() {
        String address = NetUtils.getLocalAddress();
        zookeeperOperator.remove(ZKUtils.getWorkerRegistryPath() + address);
    }

}