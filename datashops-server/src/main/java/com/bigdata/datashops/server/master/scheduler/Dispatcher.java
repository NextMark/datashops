package com.bigdata.datashops.server.master.scheduler;

import static com.bigdata.datashops.common.Constants.ZK_JOB_QUEUE;
import static com.bigdata.datashops.common.Constants.ZK_ROOT;
import static com.bigdata.datashops.common.Constants.ZK_WORKER_NODE;

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
import com.bigdata.datashops.model.pojo.job.JobInstance;
import com.bigdata.datashops.model.pojo.rpc.Host;
import com.bigdata.datashops.protocol.GrpcRequest;
import com.bigdata.datashops.server.rpc.GrpcRemotingClient;
import com.bigdata.datashops.server.master.dispatch.SelectorContext;
import com.bigdata.datashops.server.master.dispatch.selector.HostManager;
import com.bigdata.datashops.server.zookeeper.ZookeeperOperator;
import com.bigdata.datashops.service.JobInstanceService;
import com.google.protobuf.ByteString;

@Component
public class Dispatcher {
    private static final Logger LOG = LoggerFactory.getLogger(Finder.class);

    @Autowired
    private JobInstanceService jobInstanceService;

    @Autowired
    private ZookeeperOperator zookeeperOperator;

    @Autowired
    private HostManager hostManager;

    @Autowired
    private GrpcRemotingClient grpcRemotingClient;

    @Scheduled(cron = "*/10 * * * * ?")
    public void dispatch() {
        List<String> ready = zookeeperOperator.getChildrenKeys(ZK_ROOT + ZK_JOB_QUEUE);

        List<String> hosts = zookeeperOperator.getChildrenKeys(ZK_ROOT + ZK_WORKER_NODE);

        List<JobInstance> jobInstanceList = jobInstanceService.findReadyJob("");
        for (JobInstance ji : jobInstanceList) {
            SelectorContext sc = new SelectorContext();
            Host host = hostManager.select(sc);
            GrpcRequest.Request request = GrpcRequest.Request.newBuilder()
                                                  .setHost(NetUtils.getLocalAddress())
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
