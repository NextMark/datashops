package com.bigdata.datashops.server.worker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bigdata.datashops.common.utils.NetUtils;
import com.bigdata.datashops.server.zookeeper.ZookeeperOperator;

@Service
public class WorkerRegistry {
    @Autowired
    private ZookeeperOperator zookeeperOperator;

    public void registry() {
        String ip = NetUtils.getLocalAddress();
    }

    public void unRegistry() {}

}
