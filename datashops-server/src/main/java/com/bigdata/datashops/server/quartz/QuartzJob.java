package com.bigdata.datashops.server.quartz;

import static com.bigdata.datashops.common.Constants.JOB_DEFAULT_OPERATOR;

import java.time.Instant;
import java.util.Date;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bigdata.datashops.common.utils.JobUtils;
import com.bigdata.datashops.model.enums.JobInstanceType;
import com.bigdata.datashops.model.enums.RunState;
import com.bigdata.datashops.model.pojo.JobGraph;
import com.bigdata.datashops.model.pojo.JobInstance;
import com.bigdata.datashops.service.JobGraphService;
import com.bigdata.datashops.service.JobInstanceService;

@Service
public class QuartzJob implements Job {
    @Autowired
    private JobGraphService jobGraphService;

    @Autowired
    private JobInstanceService jobInstanceService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();
        Long graphId = jobDataMap.getLong("graphId");
        JobGraph graph = jobGraphService.getJobGraph(graphId);

        JobInstance instance = JobInstance.builder()
                                       .graphId(graphId)
                                       .instanceId(JobUtils.genJobInstanceId())
                                       .submitTime(new Date())
                                       .status(1)
                                       .retryTimes(graph.getRetryTimes())
                                       .retryInterval(graph.getRetryInterval())
                                       .state(RunState.PREPARE)
                                       .operator(JOB_DEFAULT_OPERATOR)
                                       .type(JobInstanceType.GRAPH.getCode())
                                       .baseTime(new Date())
                                       .build();
        jobInstanceService.save(instance);

    }
}
