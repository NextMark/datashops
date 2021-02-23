package com.bigdata.datashops.server.master.scheduler;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bigdata.datashops.common.Constants;
import com.bigdata.datashops.model.enums.RunState;
import com.bigdata.datashops.model.pojo.job.JobInstance;
import com.bigdata.datashops.server.queue.JobQueue;
import com.bigdata.datashops.service.JobInstanceService;
import com.google.common.collect.Lists;

public class Finder extends Thread {
    private static final Logger LOG = LoggerFactory.getLogger(Finder.class);

    private JobInstanceService jobInstanceService;

    public Finder(JobInstanceService jobInstanceService) {
        this.jobInstanceService = jobInstanceService;
    }

    @Override
    public void run() {
        LOG.info("Finder run.");
        List<Integer> status = Lists.newArrayList();
        for (RunState runState : RunState.values()) {
            if (RunState.WAIT_FOR_RUN.compareTo(runState) < 0) {
                break;
            }
            status.add(runState.getCode());
        }
        String filters = "state=" + StringUtils.join(status, Constants.SEPARATOR_COMMA);
        List<JobInstance> statusList = jobInstanceService.findReadyJob(filters);
        if (statusList.size() > 0) {
            LOG.info("[Finder] find {} instances, add to queue", statusList.size());
        }
        for (JobInstance instance : statusList) {
            boolean in = JobQueue.getInstance().getQueue().offer(instance);
            if (in) {
                instance.setState(RunState.RUNNING.getCode());
            } else {
                instance.setState(RunState.WAIT_FOR_RUN.getCode());
            }
            instance.setUpdateTime(new Date());
            jobInstanceService.save(instance);
        }
    }
}
