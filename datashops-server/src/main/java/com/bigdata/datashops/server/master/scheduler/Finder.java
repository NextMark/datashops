package com.bigdata.datashops.server.master.scheduler;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bigdata.datashops.common.Constants;
import com.bigdata.datashops.model.enums.RunState;
import com.bigdata.datashops.model.pojo.job.JobInstance;
import com.bigdata.datashops.server.queue.JobQueue;
import com.bigdata.datashops.server.utils.ZKUtils;
import com.bigdata.datashops.server.zookeeper.ZookeeperOperator;
import com.bigdata.datashops.service.JobInstanceService;
import com.google.common.collect.Lists;

@Component
public class Finder implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(Finder.class);

    @Autowired
    private JobInstanceService jobInstanceService;

    @Autowired
    private ZookeeperOperator zookeeperOperator;

    @Autowired
    private Checker checker;

    @Autowired
    private JobQueue jobQueue;

    @Override
    public void run() {
        LOG.info("Finder run");
        InterProcessMutex mutex = null;
        try {
            mutex = new InterProcessMutex(zookeeperOperator.getZkClient(), ZKUtils.getFinderLockPath());
            mutex.acquire();
            List<Integer> status = Lists.newArrayList();
            for (RunState runState : RunState.values()) {
                if (RunState.WAIT_FOR_DEPENDENCY.compareTo(runState) < 0) {
                    break;
                }
                status.add(runState.getCode());
            }
            String filters = "state=" + StringUtils.join(status, Constants.SEPARATOR_COMMA);

            List<JobInstance> jobInstanceList = jobInstanceService.findReadyJob(filters);
            if (jobInstanceList.size() > 0) {
                LOG.info("Find {} instances, add to queue", jobInstanceList.size());
            }
            for (JobInstance instance : jobInstanceList) {
                RunState state = checker.checkJob(instance);
                jobQueue.getQueue().put(instance.getInstanceId());
                if (state == RunState.WAIT_FOR_RUN) {
                    jobQueue.getQueue().put(instance.getInstanceId());
                }
            }
        } catch (Exception e) {
            LOG.error("master start up exception", e);
        } finally {
            zookeeperOperator.releaseMutex(mutex);
        }
    }
}
