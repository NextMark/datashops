package com.bigdata.datashops.server.job;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bigdata.datashops.common.utils.FileUtils;
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
        JobContext jobContext = new JobContext();
        jobContext.setGrpcRemotingClient(grpcRemotingClient);
        jobContext.setZookeeperOperator(zookeeperOperator);
        jobContext.setJobInstance(instance);
        jobContext.setLogger(logger);
        jobContext.setExecuteUser("root");
        jobContext.setExecutePath(FileUtils.getProcessExecDir(instance.getProjectId(), instance.getJob().getId()));
        switch (jobType) {
            case HIVE:
                return new HiveJob(jobContext);
            case SHELL:
                return new ShellJob(jobContext);
            case PYTHON:
                return new PythonJob(jobContext);
            case MYSQL:
                return new MysqlJob(jobContext);
            case SPARK:
                return new SparkJob(jobContext);
            case FLINK:
                return new FlinkJob(jobContext);
            case CLICK_HOUSE:
                return new ClickHouseJob(jobContext);
            default:
                throw new IllegalArgumentException();
        }
    }
}
