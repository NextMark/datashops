package com.bigdata.datashops.server.master.registry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bigdata.datashops.common.Constants;
import com.bigdata.datashops.common.utils.NetUtils;
import com.bigdata.datashops.server.config.BaseConfig;
import com.bigdata.datashops.server.utils.ZKUtils;
import com.bigdata.datashops.server.zookeeper.ZookeeperOperator;

@Service
public class MasterRegistry {
    @Autowired
    private ZookeeperOperator zookeeperOperator;

    @Autowired
    private BaseConfig baseConfig;

    public void registry() {
        String address = NetUtils.getLocalAddress();
        zookeeperOperator.persistEphemeral(
                ZKUtils.getMasterRegistryPath() + "/" + address + Constants.SEPARATOR_UNDERLINE + baseConfig
                                                                                                          .getMasterPort(),
                "");
    }

    public void unRegistry() {
        String address = NetUtils.getLocalAddress();
        zookeeperOperator.remove(ZKUtils.getMasterRegistryPath() + "/" + address);
    }
}
