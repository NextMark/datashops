package com.bigdata.datashops.server.worker.registry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bigdata.datashops.common.utils.NetUtils;
import com.bigdata.datashops.server.utils.ZKUtils;
import com.bigdata.datashops.service.zookeeper.ZookeeperOperator;

@Service
public class WorkerRegistry {
    @Autowired
    private ZookeeperOperator zookeeperOperator;

    public void registry() {
        //        String address = NetUtils.getLocalAddress();
        //        zookeeperOperator.persistEphemeral(
        //                ZKUtils.getWorkerRegistryPath() + "/" + address + Constants.SEPARATOR_UNDERLINE + baseConfig
        //                                                                                                          .getWorkerPort(),
        //                "");
    }

    public void unRegistry() {
        String address = NetUtils.getLocalAddress();
        zookeeperOperator.remove(ZKUtils.getWorkerRegistryPath() + "/" + address);
    }

}
