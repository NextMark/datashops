package com.bigdata.datashops.server.job;

import org.apache.spark.launcher.SparkAppHandle;
import org.apache.spark.launcher.SparkLauncher;

import com.bigdata.datashops.common.Constants;
import com.bigdata.datashops.common.utils.JSONUtils;
import com.bigdata.datashops.model.pojo.job.data.SparkData;

public class SparkJob extends AbstractYarnJob {

    private SparkData sparkData;

    private static final boolean waitForCompletion = true;

    public SparkJob(JobContext jobContext) {
        super(jobContext);
        sparkData = JSONUtils.parseObject(jobContext.getJobInstance().getJob().getData(), SparkData.class);
    }

    @Override
    protected void process() {
        try {
            System.setProperty("HADOOP_USER_NAME", "hdfs");
            SparkLauncher launcher = new SparkLauncher().setVerbose(true).setMaster("yarn").setDeployMode("cluster")
                                             .setAppName(sparkData.getName())
                                             .setConf("spark.driver.memory", sparkData.getDriverMemory())
                                             .setConf("spark.executor.memory", sparkData.getExecutorMemory())
                                             .setConf("spark.executor.cores", sparkData.getExecutorCore())
                                             .setConf("spark.yarn.queue", sparkData.getQueue())
                                             .setAppResource(sparkData.getUrl()).setMainClass(sparkData.getClassName())
                                             .addAppArgs(sparkData.getExtension().split(Constants.SEPARATOR_COMMA));

            SparkAppHandle handle = launcher.startApplication();
            LOG.info("Started application; handle={}", handle);

            while (handle.getAppId() == null) {
                LOG.info("Waiting for application to be submitted: status={}", handle.getState());
                Thread.sleep(3000L);
            }
            LOG.info("Submitted as {}", handle.getAppId());
            if (waitForCompletion) {
                while (!handle.getState().isFinal()) {
                    LOG.info("{}: status={}", handle.getAppId(), handle.getState());
                    Thread.sleep(1500L);
                }
                LOG.info("Finished as {}", handle.getState());
                success();
            } else {
                handle.disconnect();
                fail();
            }
            //            result.setData(handle.getAppId());
            //            rpc(Constants.RPC_JOB_APP_ID);
        } catch (Exception e) {
            LOG.error(String.format("Job execute error class=%s, name=%s, instanceId=%s", this.getClass(),
                    jobInstance.getJob().getName(), jobInstance.getInstanceId()), e);
            fail();
        }
    }

    @Override
    protected void after() {
    }
}
