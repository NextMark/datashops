package com.bigdata.datashops.server.job;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bigdata.datashops.model.enums.JobType;
import com.bigdata.datashops.model.pojo.job.JobInstance;
import com.bigdata.datashops.server.rpc.GrpcRemotingClient;
import com.bigdata.datashops.server.zookeeper.ZookeeperOperator;

@Component
public class JobManager {
    @Autowired
    GrpcRemotingClient grpcRemotingClient;

    @Autowired
    ZookeeperOperator zookeeperOperator;

    public AbstractJob createJob(JobInstance instance, Logger logger) {
        JobType jobType = JobType.of(instance.getType());
        switch (jobType) {
            case HIVE:
                return new HiveJob(instance, logger, grpcRemotingClient, zookeeperOperator);
            case BASH:
            case MYSQL:
                return new MysqlJob(instance, logger, grpcRemotingClient, zookeeperOperator);
            case SPARK:
            case FLINK:
            default:
                throw new IllegalArgumentException();
        }
    }
}
