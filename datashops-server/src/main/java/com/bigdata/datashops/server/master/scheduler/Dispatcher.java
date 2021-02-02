package com.bigdata.datashops.server.master.scheduler;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.bigdata.datashops.common.Constants;
import com.bigdata.datashops.common.utils.JSONUtils;
import com.bigdata.datashops.common.utils.NetUtils;
import com.bigdata.datashops.model.enums.RunState;
import com.bigdata.datashops.model.pojo.job.JobInstance;
import com.bigdata.datashops.model.pojo.rpc.Host;
import com.bigdata.datashops.protocol.GrpcRequest;
import com.bigdata.datashops.server.master.dispatch.selector.RandomHostSelector;
import com.bigdata.datashops.server.rpc.GrpcRemotingClient;
import com.bigdata.datashops.server.utils.ZKUtils;
import com.bigdata.datashops.server.zookeeper.ZookeeperOperator;
import com.bigdata.datashops.service.JobInstanceService;
import com.google.common.collect.Lists;
import com.google.protobuf.ByteString;

@Component
public class Dispatcher {
    private static final Logger LOG = LoggerFactory.getLogger(Finder.class);

    @Autowired
    private JobInstanceService jobInstanceService;

    @Autowired
    private ZookeeperOperator zookeeperOperator;

    //    @Autowired
    //    private HostManager hostManager;
    @Autowired
    private Checker checker;

    @Autowired
    private GrpcRemotingClient grpcRemotingClient;

    @Scheduled(cron = "*/10 * * * * ?")
    public void dispatch() {
        //List<String> ready = zookeeperOperator.getChildrenKeys(ZKUtils.getQueuePath());

        LOG.info("Dispatch run.");
        boolean workerExist = zookeeperOperator.isExisted(ZKUtils.getWorkerRegistryPath());
        if (!workerExist) {
            return;
        }
        List<String> hostsStr = zookeeperOperator.getChildrenKeys(ZKUtils.getWorkerRegistryPath());
        List<Host> hosts = Lists.newArrayList();
        for (String host : hostsStr) {
            String[] hostInfo = host.split(Constants.SEPARATOR_SEMICOLON);
            Host h = Host.builder().ip(hostInfo[0]).port(Integer.parseInt(hostInfo[1])).build();
            hosts.add(h);
        }

        String filter = "status=1;state<4;type=1";
        List<JobInstance> jobInstanceList = jobInstanceService.findReadyJob(filter);
        // 根据优先级排序
        Collections.sort(jobInstanceList);
        for (JobInstance ji : jobInstanceList) {
            boolean ready = checker.check(ji);
            if (!ready) {
                ji.setUpdateTime(new Date());
                ji.setState(RunState.WAIT_FOR_DEPENDENCY.getCode());
                jobInstanceService.save(ji);
                continue;
            }
            RandomHostSelector randomHostSelector = new RandomHostSelector(hosts);
            Host host = randomHostSelector.select();
            GrpcRequest.Request request = GrpcRequest.Request.newBuilder().setHost(NetUtils.getLocalAddress())
                                                  .setRequestId(RandomUtils.nextInt())
                                                  .setRequestType(GrpcRequest.RequestType.JOB_EXECUTE_REQUEST)
                                                  .setBody(ByteString.copyFrom(JSONUtils.toJsonString(ji).getBytes()))
                                                  .build();
            GrpcRequest.Response response = grpcRemotingClient.send(request, host);
            if (response.getStatus() != Constants.RPC_SUCCESS) {

            }
        }
    }
}
