package com.bigdata.datashops.server.master.scheduler;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bigdata.datashops.common.Constants;
import com.bigdata.datashops.common.utils.JSONUtils;
import com.bigdata.datashops.common.utils.NetUtils;
import com.bigdata.datashops.common.utils.PropertyUtils;
import com.bigdata.datashops.model.enums.HostSelector;
import com.bigdata.datashops.model.enums.RunState;
import com.bigdata.datashops.model.pojo.job.JobInstance;
import com.bigdata.datashops.model.pojo.rpc.Host;
import com.bigdata.datashops.protocol.GrpcRequest;
import com.bigdata.datashops.remote.rpc.GrpcRemotingClient;
import com.bigdata.datashops.server.master.selector.AssignSelector;
import com.bigdata.datashops.server.master.selector.RandomHostSelector;
import com.bigdata.datashops.server.master.selector.ScoreSelector;
import com.bigdata.datashops.server.utils.ZKUtils;
import com.bigdata.datashops.service.JobInstanceService;
import com.bigdata.datashops.service.zookeeper.ZookeeperOperator;
import com.google.common.collect.Lists;
import com.google.protobuf.ByteString;

@Component
public class Dispatcher {
    private static final Logger LOG = LoggerFactory.getLogger(Finder.class);

    @Autowired
    private JobInstanceService jobInstanceService;

    @Autowired
    private ZookeeperOperator zookeeperOperator;

    @Autowired
    private GrpcRemotingClient grpcRemotingClient;

    public void dispatch(String instanceId) {
        LOG.info("Dispatch instance {}", instanceId);
        boolean workerExist = zookeeperOperator.isExisted(ZKUtils.getWorkerRegistryPath());
        if (!workerExist) {
            return;
        }
        List<String> hostsStr = zookeeperOperator.getChildrenKeys(ZKUtils.getWorkerRegistryPath());
        if (hostsStr.size() == 0) {
            LOG.warn("No active worker in {}", ZKUtils.getWorkerRegistryPath());
            return;
        }
        List<Host> hosts = Lists.newArrayList();
        for (String host : hostsStr) {
            String[] hostInfo = host.split(Constants.SEPARATOR_UNDERLINE);
            Host h = new Host();
            h.setIp(hostInfo[0]);
            h.setPort(Integer.parseInt(hostInfo[1]));
            hosts.add(h);
        }
        JobInstance instance = jobInstanceService.findOneByQuery("instanceId=" + instanceId);
        jobInstanceService.fillJob(Collections.singletonList(instance));

        //        JobType jobType = JobType.of(instance.getType());
        //        if (jobType == JobType.HIVE || jobType == JobType.MYSQL || jobType == JobType.CLICK_HOUSE) {
        //            instance.getJob().setData(SQLParser.parseSQL(instance.getJob().getData()));
        //        }

        // select host
        Host host = new Host();
        HostSelector selector = HostSelector.of(instance.getJob().getHostSelector());
        switch (selector) {
            case ASSIGN:
                host.setIp(instance.getJob().getHost());
                host.setPort(PropertyUtils.getInt(Constants.WORKER_GRPC_SERVER_PORT));
                host = new AssignSelector().select(Collections.singleton(host));
                break;
            case SCORE:
                host = new ScoreSelector().select(hosts);
                break;
            default:
                host = new RandomHostSelector().select(hosts);
        }
        instance.setHost(host.getIp());
        instance.setState(RunState.RUNNING.getCode());
        instance.setStartTime(new Date());
        jobInstanceService.saveEntity(instance);
        GrpcRequest.Request request = GrpcRequest.Request.newBuilder().setIp(NetUtils.getLocalAddress())
                                              .setPort(PropertyUtils.getInt(Constants.MASTER_GRPC_SERVER_PORT))
                                              .setRequestId(instanceId)
                                              .setRequestType(GrpcRequest.RequestType.JOB_EXECUTE_REQUEST)
                                              .setBody(ByteString.copyFrom(JSONUtils.toJsonString(instance).getBytes()))
                                              .build();
        GrpcRequest.Response response = grpcRemotingClient.send(request, host);
    }

}
