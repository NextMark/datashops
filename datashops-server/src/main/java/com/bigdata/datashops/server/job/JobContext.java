package com.bigdata.datashops.server.job;

import org.slf4j.Logger;

import com.bigdata.datashops.model.pojo.job.JobInstance;
import com.bigdata.datashops.server.config.BaseConfig;
import com.bigdata.datashops.server.rpc.GrpcRemotingClient;
import com.bigdata.datashops.server.zookeeper.ZookeeperOperator;

import lombok.Data;

@Data
public class JobContext {
    private JobInstance jobInstance;

    private ZookeeperOperator zookeeperOperator;

    private GrpcRemotingClient grpcRemotingClient;

    private Logger logger;

    private String executePath;

    private String executeUser;

    private BaseConfig baseConfig;

}
