package com.bigdata.datashops.server.quartz;

import static com.bigdata.datashops.common.Constants.JOB_DEFAULT_OPERATOR;

import java.util.Date;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bigdata.datashops.common.utils.JobUtils;
import com.bigdata.datashops.model.enums.JobInstanceType;
import com.bigdata.datashops.model.enums.RunState;
import com.bigdata.datashops.model.pojo.job.JobInstance;
import com.bigdata.datashops.server.utils.JobHelper;
import com.bigdata.datashops.service.JobGraphService;
import com.bigdata.datashops.service.JobInstanceService;
import com.bigdata.datashops.service.JobService;

@Service
public class QuartzJob implements Job {
    @Autowired
    private JobGraphService jobGraphService;

    @Autowired
    private JobInstanceService jobInstanceService;

    @Autowired
    private JobService jobService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void execute(JobExecutionContext jobExecutionContext) {
        JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();
        String maskId = jobDataMap.getString("maskId");
        com.bigdata.datashops.model.pojo.job.Job job = jobService.getJobByMaskId(maskId);
        String instanceId = JobUtils.genJobInstanceId();
        Date now = new Date();
        Date bizDate = JobHelper.getBizDate(job.getSchedulingPeriod());

        JobInstance instance = JobInstance.builder().maskId(maskId).instanceId(instanceId).submitTime(now).status(1)
                                       .retryTimes(job.getRetryTimes()).retryInterval(job.getRetryInterval())
                                       .state(RunState.CREATED.getCode()).operator(JOB_DEFAULT_OPERATOR)
                                       .type(JobInstanceType.GRAPH.getCode()).bizTime(bizDate)
                                       .priority(job.getPriority()).build();
        jobInstanceService.save(instance);
    }
}
