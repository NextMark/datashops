package com.bigdata.datashops.server.master.scheduler;

import org.apache.curator.framework.recipes.queue.DistributedPriorityQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bigdata.datashops.server.rpc.GrpcRemotingClient;
import com.bigdata.datashops.server.utils.RedisUtils;
import com.bigdata.datashops.server.zookeeper.ZookeeperOperator;
import com.bigdata.datashops.service.JobInstanceService;

@Component
public class Dispatcher implements Runnable {
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

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public void run() {
        //List<String> ready = zookeeperOperator.getChildrenKeys(ZKUtils.getQueuePath());

        LOG.info("Dispatch run.");
        //        boolean workerExist = zookeeperOperator.isExisted(ZKUtils.getWorkerRegistryPath());
        //        if (!workerExist) {
        //            return;
        //        }
        //        List<String> hostsStr = zookeeperOperator.getChildrenKeys(ZKUtils.getWorkerRegistryPath());
        //        List<Host> hosts = Lists.newArrayList();
        //        for (String host : hostsStr) {
        //            String[] hostInfo = host.split(Constants.SEPARATOR_SEMICOLON);
        //            Host h = Host.builder().ip(hostInfo[0]).port(Integer.parseInt(hostInfo[1])).build();
        //            hosts.add(h);
        //        }
        //
        //        String filter = "status=1;state<4;type=1";
        //        List<JobInstance> jobInstanceList = jobInstanceService.findReadyJob(filter);
        //        // 根据优先级排序
        //        Collections.sort(jobInstanceList);
        //        for (JobInstance ji : jobInstanceList) {
        //            boolean ready = checker.check(ji);
        //            if (!ready) {
        //                ji.setUpdateTime(new Date());
        //                ji.setState(RunState.WAIT_FOR_DEPENDENCY.getCode());
        //                jobInstanceService.save(ji);
        //                continue;
        //            }
        //            RandomHostSelector randomHostSelector = new RandomHostSelector(hosts);
        //            Host host = randomHostSelector.select();
        //            GrpcRequest.Request request = GrpcRequest.Request.newBuilder().setHost(NetUtils.getLocalAddress())
        //                                                  .setRequestId(RandomUtils.nextInt())
        //                                                  .setRequestType(GrpcRequest.RequestType.JOB_EXECUTE_REQUEST)
        //                                                  .setBody(ByteString.copyFrom(JSONUtils.toJsonString(ji)
        //                                                  .getBytes()))
        //                                                  .build();
        //            GrpcRequest.Response response = grpcRemotingClient.send(request, host);
        //            if (response.getStatus() != Constants.RPC_SUCCESS) {
        //
        //            }
        //        }

        DistributedPriorityQueue<String> queue = null;

        //        try {
        //            QueueConsumer<String> consumer = zookeeperOperator.createQueueConsumer();
        //            QueueBuilder<String> builder = QueueBuilder.builder(zookeeperOperator.getZkClient(), consumer,
        //                    zookeeperOperator.createQueueSerializer(), ZKUtils.getQueuePath());
        //            queue = builder.buildPriorityQueue(0);
        //            queue.start();
        //        } catch (Exception e) {
        //
        //        } finally {
        //            CloseableUtils.closeQuietly(queue);
        //        }
    }

}
