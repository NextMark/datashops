package com.bigdata.datashops.server.job;

import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;

import com.bigdata.datashops.common.Constants;
import com.bigdata.datashops.common.utils.JSONUtils;
import com.bigdata.datashops.common.utils.NetUtils;
import com.bigdata.datashops.dao.datasource.HiveDataSource;
import com.bigdata.datashops.model.pojo.job.JobInstance;
import com.bigdata.datashops.protocol.GrpcRequest;
import com.bigdata.datashops.server.rpc.GrpcRemotingClient;
import com.bigdata.datashops.server.zookeeper.ZookeeperOperator;
import com.google.protobuf.ByteString;

public class HiveJob extends AbstractJob {

    public HiveJob(JobInstance instance, Logger logger, GrpcRemotingClient grpcRemotingClient,
                   ZookeeperOperator zookeeperOperator) {
        super(instance, logger, grpcRemotingClient, zookeeperOperator);
    }

    @Override
    public void before() {
        LOG.info("Job before");
    }

    @Override
    public void handle() {
        String data = instance.getData();
        baseDataSource = JSONUtils.parseObject(data, HiveDataSource.class);
        executeSQL();
    }

    private void executeSQL() {

    }

    @Override
    public void after() {
        LOG.info("Job end");
        request =
                GrpcRequest.Request.newBuilder().setHost(NetUtils.getLocalAddress()).setRequestId(RandomUtils.nextInt())
                        .setRequestType(GrpcRequest.RequestType.JOB_EXECUTE_RESPONSE).setCode(Constants.RPC_JOB_SUCCESS)
                        .setBody(ByteString.copyFrom(JSONUtils.toJsonString(result).getBytes())).build();
        grpcRemotingClient.send(request, selectHost());
    }
}
