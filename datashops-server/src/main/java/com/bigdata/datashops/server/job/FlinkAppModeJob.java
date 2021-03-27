package com.bigdata.datashops.server.job;

import java.util.Collections;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.flink.client.deployment.ClusterDeploymentException;
import org.apache.flink.client.deployment.ClusterSpecification;
import org.apache.flink.client.deployment.application.ApplicationConfiguration;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.client.program.ClusterClientProvider;
import org.apache.flink.configuration.CheckpointingOptions;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.CoreOptions;
import org.apache.flink.configuration.DeploymentOptions;
import org.apache.flink.configuration.GlobalConfiguration;
import org.apache.flink.configuration.JobManagerOptions;
import org.apache.flink.configuration.MemorySize;
import org.apache.flink.configuration.PipelineOptions;
import org.apache.flink.configuration.TaskManagerOptions;
import org.apache.flink.yarn.YarnClientYarnClusterInformationRetriever;
import org.apache.flink.yarn.YarnClusterDescriptor;
import org.apache.flink.yarn.YarnClusterInformationRetriever;
import org.apache.flink.yarn.configuration.YarnConfigOptions;
import org.apache.flink.yarn.configuration.YarnDeploymentTarget;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;

import com.bigdata.datashops.common.Constants;
import com.bigdata.datashops.common.utils.JSONUtils;
import com.bigdata.datashops.common.utils.PropertyUtils;
import com.bigdata.datashops.model.pojo.job.data.FlinkData;

public class FlinkAppModeJob extends AbstractJob {
    public FlinkAppModeJob(JobContext jobContext) {
        super(jobContext);
        flinkData = JSONUtils.parseObject(jobContext.getJobInstance().getData(), FlinkData.class);
    }

    private FlinkData flinkData;

    @Override
    protected void process() {
        YarnClient yarnClient = YarnClient.createYarnClient();
        YarnConfiguration yarnConfiguration = new YarnConfiguration();
        yarnConfiguration
                .addResource(new Path(String.format("%s/conf/%s", System.getProperty("user.dir"), "yarn-site.xml")));
        yarnConfiguration
                .addResource(new Path(String.format("%s/conf/%s", System.getProperty("user.dir"), "hdfs-site.xml")));
        yarnConfiguration
                .addResource(new Path(String.format("%s/conf/%s", System.getProperty("user.dir"), "core-site.xml")));

        yarnClient.init(yarnConfiguration);
        yarnClient.start();

        YarnClusterInformationRetriever clusterInformationRetriever =
                YarnClientYarnClusterInformationRetriever.create(yarnClient);

        Configuration flinkConfiguration = GlobalConfiguration.loadConfiguration(
                String.format("%s%s", System.getProperty("user.dir"), "/conf/flink"));
        flinkConfiguration.set(CheckpointingOptions.INCREMENTAL_CHECKPOINTS, true);
        flinkConfiguration.set(PipelineOptions.JARS, Collections.singletonList(flinkData.getUrl()));

        Path remoteLib = new Path(PropertyUtils.getString(Constants.FLINK_LIBS_PATH));
        flinkConfiguration.set(YarnConfigOptions.PROVIDED_LIB_DIRS, Collections.singletonList(remoteLib.toString()));

        //flinkConfiguration.set(YarnConfigOptions.FLINK_DIST_JAR, flinkDistJar);
        flinkConfiguration.set(DeploymentOptions.TARGET, YarnDeploymentTarget.APPLICATION.getName());
        flinkConfiguration.set(YarnConfigOptions.APPLICATION_NAME, flinkData.getYarnAppName());

        if (StringUtils.isNotEmpty(flinkData.getJobManagerMemory())) {
            flinkConfiguration
                    .set(JobManagerOptions.TOTAL_FLINK_MEMORY, MemorySize.parse(flinkData.getJobManagerMemory()));
        }
        if (StringUtils.isNotEmpty(flinkData.getTaskManagerMemory())) {
            flinkConfiguration
                    .set(TaskManagerOptions.TOTAL_FLINK_MEMORY, MemorySize.parse(flinkData.getTaskManagerMemory()));
        }
        if (StringUtils.isNotEmpty(flinkData.getYarnQueue())) {
            flinkConfiguration.set(YarnConfigOptions.APPLICATION_QUEUE, flinkData.getYarnQueue());
        }
        if (!Objects.isNull(flinkData.getTaskSlotNum())) {
            flinkConfiguration.set(TaskManagerOptions.NUM_TASK_SLOTS, flinkData.getTaskSlotNum());
        }
        if (!Objects.isNull(flinkData.getParallelism())) {
            flinkConfiguration.set(CoreOptions.DEFAULT_PARALLELISM, flinkData.getParallelism());
        }

        ClusterSpecification clusterSpecification =
                new ClusterSpecification.ClusterSpecificationBuilder().createClusterSpecification();
        ApplicationConfiguration appConfig = new ApplicationConfiguration(null, flinkData.getClassName());
        YarnClusterDescriptor yarnClusterDescriptor =
                new YarnClusterDescriptor(flinkConfiguration, yarnConfiguration, yarnClient,
                        clusterInformationRetriever, true);
        ClusterClientProvider<ApplicationId> clusterClientProvider = null;
        try {
            clusterClientProvider = yarnClusterDescriptor.deployApplicationCluster(clusterSpecification, appConfig);
        } catch (ClusterDeploymentException e) {
            e.printStackTrace();
        }

        ClusterClient<ApplicationId> clusterClient = clusterClientProvider.getClusterClient();
        ApplicationId applicationId = clusterClient.getClusterId();
        LOG.info("flink submit app, id {}", applicationId);
    }

    @Override
    public void after() {
        buildGrpcRequest(Constants.RPC_JOB_SUCCESS);
    }
}