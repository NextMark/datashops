package com.bigdata.datashops.server.worker.heartbeat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bigdata.datashops.common.Constants;
import com.bigdata.datashops.common.utils.JSONUtils;
import com.bigdata.datashops.common.utils.NetUtils;
import com.bigdata.datashops.common.utils.PropertyUtils;
import com.bigdata.datashops.model.pojo.rpc.OSInfo;
import com.bigdata.datashops.server.utils.OSUtils;
import com.bigdata.datashops.server.utils.ZKUtils;
import com.bigdata.datashops.service.zookeeper.ZookeeperOperator;

import lombok.extern.slf4j.Slf4j;
import oshi.util.FormatUtil;

@Component
@Slf4j
public class WorkerHeartBeat implements Runnable {
    @Autowired
    private ZookeeperOperator zookeeperOperator;

    @Override
    public void run() {
        String address = NetUtils.getLocalAddress();
        log.info("Worker heartbeat {}", address);
        // TODO 计算权重
        OSInfo osInfo = new OSInfo();
        osInfo.setType("worker");
        osInfo.setIp(NetUtils.getLocalAddress());
        osInfo.setHostName(OSUtils.getHostName());
        osInfo.setAvailableMem(FormatUtil.formatBytes(OSUtils.getAvailableMem()));
        osInfo.setTotalMem(FormatUtil.formatBytes(OSUtils.getTotalMem()));
        osInfo.setName(OSUtils.getName());
        osInfo.setVersion(OSUtils.getVersion());
        osInfo.setCpuInfo(JSONUtils.toJsonString(OSUtils.getCpuInfo()));

        zookeeperOperator.persistEphemeral(String.format("%s/%s_%s", ZKUtils.getWorkerRegistryPath(), address,
                PropertyUtils.getInt(Constants.WORKER_GRPC_SERVER_PORT)), JSONUtils.toJsonString(osInfo));
    }
}
