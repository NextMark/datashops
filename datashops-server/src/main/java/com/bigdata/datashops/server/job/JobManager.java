package com.bigdata.datashops.server.job;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bigdata.datashops.common.utils.FileUtils;
import com.bigdata.datashops.model.enums.JobType;
import com.bigdata.datashops.model.pojo.job.Job;
import com.bigdata.datashops.model.pojo.job.JobInstance;
import com.bigdata.datashops.remote.rpc.GrpcRemotingClient;
import com.bigdata.datashops.service.JobService;
import com.bigdata.datashops.service.zookeeper.ZookeeperOperator;

@Component
public class JobManager {
    @Autowired
    GrpcRemotingClient grpcRemotingClient;

    @Autowired
    ZookeeperOperator zookeeperOperator;

    @Autowired
    protected JobService jobService;

    public AbstractJob createJob(JobInstance instance, Logger logger) {
        Job job = jobService.getOnlineJobByMaskId(instance.getMaskId());
        JobType jobType = JobType.of(job.getType());
        JobContext jobContext = new JobContext();
        jobContext.setGrpcRemotingClient(grpcRemotingClient);
        jobContext.setZookeeperOperator(zookeeperOperator);
        jobContext.setJobInstance(instance);
        jobContext.setLogger(logger);
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
            case KAFKA_2_HDFS:
            case FSQL:
                return new FlinkAppModeJob(jobContext);
            case CLICK_HOUSE:
                return new ClickHouseJob(jobContext);
            case HIVE_2_MYSQL:
            case MYSQL_2_HIVE:
                return new SqoopJob(jobContext);
            default:
                throw new IllegalArgumentException();
        }
    }
}
