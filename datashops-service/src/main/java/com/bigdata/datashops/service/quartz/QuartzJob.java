package com.bigdata.datashops.service.quartz;

import static com.bigdata.datashops.common.Constants.JOB_DEFAULT_OPERATOR;

import java.util.Date;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bigdata.datashops.common.utils.JobUtils;
import com.bigdata.datashops.model.enums.JobInstanceType;
import com.bigdata.datashops.model.enums.RunState;
import com.bigdata.datashops.model.pojo.job.JobInstance;
import com.bigdata.datashops.service.JobInstanceService;
import com.bigdata.datashops.service.JobService;
import com.bigdata.datashops.service.utils.JobHelper;

@Service
public class QuartzJob implements Job {
    private static Logger LOG = LoggerFactory.getLogger(QuartzJob.class);

    @Autowired
    private JobInstanceService jobInstanceService;

    @Autowired
    private JobService jobService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();
        int projectId = jobDataMap.getInt("projectId");
        int jobId = jobDataMap.getInt("jobId");
        LOG.info("Run job, projectId {}, jobId {}", projectId, jobId);
        com.bigdata.datashops.model.pojo.job.Job job = jobService.getJobByProjectIdAndId(projectId, jobId);
        String instanceId = JobUtils.genJobInstanceId();
        Date now = new Date();
        Date bizDate = JobHelper.getBizDate(job.getSchedulingPeriod());

        JobInstance instance =
                JobInstance.builder().maskId(job.getMaskId()).instanceId(instanceId).submitTime(now).status(1)
                        .name(job.getName()).priority(job.getPriority()).projectId(projectId)
                        .retryTimes(job.getRetryTimes()).retryInterval(job.getRetryInterval())
                        .state(RunState.CREATED.getCode()).operator(JOB_DEFAULT_OPERATOR)
                        .type(JobInstanceType.GRAPH.getCode()).bizTime(bizDate).priority(job.getPriority()).build();
        jobInstanceService.save(instance);
    }
}
