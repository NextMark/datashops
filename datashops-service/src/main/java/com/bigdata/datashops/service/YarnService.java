package com.bigdata.datashops.service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.apache.flink.api.common.JobID;
import org.apache.flink.client.cli.CliArgsException;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.FlinkException;
import org.apache.flink.yarn.YarnClusterClientFactory;
import org.apache.flink.yarn.YarnClusterDescriptor;
import org.apache.flink.yarn.configuration.YarnConfigOptions;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.springframework.stereotype.Service;

@Service
public class YarnService {
    public void kill(String appId) throws FlinkException, CliArgsException, ExecutionException, InterruptedException {
        String jobid = "1f5d2fd883d90299365e19de7051dece";
        String savePoint = "hdfs://localhost/flink-savepoints";

        Configuration flinkConfiguration = new Configuration();
        flinkConfiguration.set(YarnConfigOptions.APPLICATION_ID, appId);
        YarnClusterClientFactory clusterClientFactory = new YarnClusterClientFactory();
        ApplicationId applicationId = clusterClientFactory.getClusterId(flinkConfiguration);
        if (applicationId == null) {
            throw new FlinkException(
                    "No cluster id was specified. Please specify a cluster to which you would like to connect.");
        }

        YarnClusterDescriptor clusterDescriptor = clusterClientFactory.createClusterDescriptor(flinkConfiguration);
        ClusterClient<ApplicationId> clusterClient = clusterDescriptor.retrieve(applicationId).getClusterClient();

        JobID jobID = parseJobId(jobid);

        CompletableFuture<String> completableFuture = clusterClient.stopWithSavepoint(jobID, true, savePoint);

        String savepoint = completableFuture.get();
        System.out.println(savepoint);
    }

    private JobID parseJobId(String jobIdString) throws CliArgsException {
        if (jobIdString == null) {
            throw new CliArgsException("Missing JobId");
        }

        final JobID jobId;
        try {
            jobId = JobID.fromHexString(jobIdString);
        } catch (IllegalArgumentException e) {
            throw new CliArgsException(e.getMessage());
        }
        return jobId;
    }

}
