package com.bigdata.datashops.server.master.registry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bigdata.datashops.common.utils.NetUtils;
import com.bigdata.datashops.server.utils.ZKUtils;
import com.bigdata.datashops.server.zookeeper.ZookeeperOperator;

@Service
public class MasterRegistry {
    @Autowired
    private ZookeeperOperator zookeeperOperator;

    public void registry() {
        String address = NetUtils.getLocalAddress();
        zookeeperOperator.persistEphemeral(ZKUtils.getMasterRegistryPath() + address, "");
    }

    public void unRegistry() {
        String address = NetUtils.getLocalAddress();
        zookeeperOperator.remove(ZKUtils.getMasterRegistryPath() + address);
    }
}
