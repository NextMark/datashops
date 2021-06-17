package com.bigdata.datashops.server.master.scheduler;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bigdata.datashops.model.enums.RunState;
import com.bigdata.datashops.model.pojo.job.JobInstance;
import com.bigdata.datashops.server.utils.ZKUtils;
import com.bigdata.datashops.service.JobInstanceService;
import com.bigdata.datashops.service.hadoop.HadoopService;
import com.bigdata.datashops.service.zookeeper.ZookeeperOperator;

@Component
public class RunningChecker implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(RunningChecker.class);

    @Autowired
    private JobInstanceService jobInstanceService;

    @Autowired
    private ZookeeperOperator zookeeperOperator;

    @Autowired
    protected HadoopService hadoopService;

    @Override
    public void run() {
        InterProcessMutex mutex = null;
        try {
            mutex = new InterProcessMutex(zookeeperOperator.getZkClient(), ZKUtils.getRunningCheckerLockPath());
            mutex.acquire();
            String filters = "appId!='';state=" + RunState.RUNNING.getCode();
            List<JobInstance> jobInstanceList = jobInstanceService.findReadyJob(filters);
            if (jobInstanceList.size() > 0) {
                LOG.info("Check running status {} instances", jobInstanceList.size());
            }
            for (JobInstance instance : jobInstanceList) {
                if (StringUtils.isNotEmpty(instance.getAppId())) {
                    try {
                        RunState state = hadoopService.getApplicationStatus(instance.getAppId());
                        LOG.info("Check {}, {}", instance.getAppId(), state);
                        if (RunState.RUNNING == state) {
                            continue;
                        }
                        instance.setState(state.getCode());
                        jobInstanceService.saveEntity(instance);
                    } catch (Exception ignored) {
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("check running status exception", e);
        } finally {
            zookeeperOperator.releaseMutex(mutex);
        }
    }
}
