package com.bigdata.datashops.server.quartz;

import static com.bigdata.datashops.common.Constants.JOB_DEFAULT_OPERATOR;

import java.util.Date;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bigdata.datashops.common.utils.JobUtils;
import com.bigdata.datashops.model.enums.JobInstanceType;
import com.bigdata.datashops.model.enums.RunState;
import com.bigdata.datashops.model.pojo.JobGraph;
import com.bigdata.datashops.model.pojo.JobInstance;
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
        Integer graphId = jobDataMap.getInt("graphId");
        JobGraph graph = jobGraphService.getJobGraph(graphId);
        String instanceId = JobUtils.genJobInstanceId();
        Date now = new Date();
        Date bizDate = JobHelper.getBizDate(graph.getSchedulingPeriod());

        JobInstance instance = JobInstance.builder()
                                       .graphId(graphId)
                                       .instanceId(instanceId)
                                       .submitTime(now)
                                       .status(1)
                                       .retryTimes(graph.getRetryTimes())
                                       .retryInterval(graph.getRetryInterval())
                                       .state(RunState.CREATED.getCode())
                                       .operator(JOB_DEFAULT_OPERATOR)
                                       .type(JobInstanceType.GRAPH.getCode())
                                       .bizTime(bizDate)
                                       .priority(graph.getPriority())
                                       .build();
        jobInstanceService.save(instance);

        String filter = String.format("graphId=%d;status=1", graphId);
        List<com.bigdata.datashops.model.pojo.Job> jobs = jobService.findJobs(filter);
        for (com.bigdata.datashops.model.pojo.Job job : jobs) {
            JobInstance ji = JobInstance.builder()
                                     .graphId(graphId)
                                     .jobId(job.getId())
                                     .instanceId(instanceId)
                                     .submitTime(now)
                                     .status(1)
                                     .retryTimes(graph.getRetryTimes())
                                     .retryInterval(graph.getRetryInterval())
                                     .state(RunState.CREATED.getCode())
                                     .operator(JOB_DEFAULT_OPERATOR)
                                     .type(JobInstanceType.JOB.getCode())
                                     .bizTime(bizDate)
                                     .priority(graph.getPriority())
                                     .build();
            jobInstanceService.save(ji);
        }
    }
}
