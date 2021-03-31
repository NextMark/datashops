package com.bigdata.datashops.server.job;

import org.slf4j.Logger;

import com.bigdata.datashops.model.pojo.job.JobInstance;
import com.bigdata.datashops.remote.rpc.GrpcRemotingClient;
import com.bigdata.datashops.service.zookeeper.ZookeeperOperator;

import lombok.Data;

@Data
public class JobContext {
    private JobInstance jobInstance;

    private ZookeeperOperator zookeeperOperator;

    private GrpcRemotingClient grpcRemotingClient;

    private Logger logger;

    private String executePath;

    private String executeUser;

}
