package com.bigdata.datashops.service.quartz;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bigdata.datashops.common.Constants;
import com.bigdata.datashops.model.pojo.job.JobInstance;
import com.bigdata.datashops.service.JobInstanceService;

@Service
public class QuartzJob implements Job {
    private static Logger LOG = LoggerFactory.getLogger(QuartzJob.class);

    @Autowired
    private JobInstanceService jobInstanceService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();
        int projectId = jobDataMap.getInt("projectId");
        int jobId = jobDataMap.getInt("jobId");
        LOG.info("Run job, projectId={}, jobId={}", projectId, jobId);

        JobInstance instance = jobInstanceService.createNewJobInstance(jobId, Constants.JOB_DEFAULT_OPERATOR);
        jobInstanceService.save(instance);
    }
}
