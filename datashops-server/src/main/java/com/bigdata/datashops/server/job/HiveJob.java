package com.bigdata.datashops.server.job;

import org.slf4j.Logger;

import com.bigdata.datashops.common.utils.JSONUtils;
import com.bigdata.datashops.dao.datasource.HiveDataSource;
import com.bigdata.datashops.model.pojo.job.JobInstance;
import com.bigdata.datashops.server.rpc.GrpcRemotingClient;
import com.bigdata.datashops.server.zookeeper.ZookeeperOperator;

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
    }
}
