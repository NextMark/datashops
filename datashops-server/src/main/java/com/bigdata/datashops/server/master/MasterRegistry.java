package com.bigdata.datashops.server.master;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bigdata.datashops.common.utils.NetUtils;
import com.bigdata.datashops.server.zookeeper.ZookeeperOperator;

@Service
public class MasterRegistry {
    @Autowired
    private ZookeeperOperator zookeeperOperator;

    public void registry() {
        String address = NetUtils.getLocalAddress();
        zookeeperOperator.persist(address, "");

    }

    public void unRegistry() {

    }
}
