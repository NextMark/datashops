package com.bigdata.datashops.server.master.scheduler;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bigdata.datashops.common.Constants;
import com.bigdata.datashops.common.utils.LocalDateUtils;
import com.bigdata.datashops.model.enums.JobInstanceType;
import com.bigdata.datashops.model.enums.RunState;
import com.bigdata.datashops.model.enums.SchedulingPeriod;
import com.bigdata.datashops.model.pojo.job.Job;
import com.bigdata.datashops.model.pojo.job.JobGraph;
import com.bigdata.datashops.model.pojo.job.JobInstance;
import com.bigdata.datashops.service.JobGraphService;
import com.bigdata.datashops.service.JobInstanceService;
import com.bigdata.datashops.service.JobService;

@Service
public class Checker {
    private static final Logger LOG = LoggerFactory.getLogger(Checker.class);

    @Autowired
    private JobGraphService jobGraphService;

    @Autowired
    private JobService jobService;

    @Autowired
    private JobInstanceService jobInstanceService;

    public boolean check(JobInstance jobInstance) {
        String preDependency = jobInstance.getPreDependency();
        if (StringUtils.isBlank(preDependency)) {
            LOG.info("[Checker] check {} pass, rely on {}.", jobInstance.getInstanceId(),
                    jobInstance.getPreDependency());
            return true;
        }
        Date bizTime = jobInstance.getBizTime();
        String[] dependencies = preDependency.split(Constants.SEPARATOR_COMMA);
        for (String s : dependencies) {
            String[] dependency = s.split(Constants.SEPARATOR_LINE);
            int nodeType = Integer.parseInt(dependency[0]);
            int preJobId = Integer.parseInt(dependency[1]);
            int offset = Integer.parseInt(dependency[2]);
            JobInstanceType jit = JobInstanceType.of(nodeType);
            Date dependencyBizTime;
            StringBuilder sb = new StringBuilder();
            if (jit == JobInstanceType.GRAPH) {
                JobGraph jobGraph = jobGraphService.getJobGraph(preJobId);
                int schedulingPeriod = jobGraph.getSchedulingPeriod();
                dependencyBizTime = getDependencyBizTime(bizTime, schedulingPeriod, offset);
                sb.append("graphId=").append(preJobId).append(";");
                sb.append("bizTime=").append(dependencyBizTime).append(";");
                sb.append("type=").append(JobInstanceType.GRAPH.getCode());
            }
            if (jit == JobInstanceType.JOB) {
                Job preJob = jobService.getJob(preJobId);
                int schedulingPeriod = preJob.getSchedulingPeriod();
                dependencyBizTime = getDependencyBizTime(bizTime, schedulingPeriod, offset);
                sb.append("jobId=").append(preJobId).append(";");
                sb.append("bizTime=").append(dependencyBizTime).append(";");
                sb.append("type=").append(JobInstanceType.JOB.getCode());
            }

            JobInstance relyOn = jobInstanceService.findJobInstance(sb.toString());
            if (RunState.of(relyOn.getState()) != RunState.SUCCESS) {
                LOG.info("[Checker] dependency not ready, instance id {}, biz time {}", jobInstance.getInstanceId(),
                        relyOn.getBizTime());
                return false;
            }
        }
        return true;
    }

    private Date getDependencyBizTime(Date date, int schedulingPeriod, int offset) {
        if (offset == 0) {
            return date;
        }
        LocalDateTime ldt = LocalDateUtils.dateToLocalDateTime(date);
        LocalDateTime bizTime = ldt;
        switch (SchedulingPeriod.of(schedulingPeriod)) {
            case MINUTE:
                bizTime = LocalDateUtils.plus(ldt, offset, ChronoUnit.MINUTES);
                break;
            case HOUR:
                bizTime = LocalDateUtils.plus(ldt, offset, ChronoUnit.HOURS);
                break;
            case DAY:
                bizTime = LocalDateUtils.plus(ldt, offset, ChronoUnit.DAYS);
                break;
            case WEEK:
                bizTime = LocalDateUtils.plus(ldt, offset, ChronoUnit.WEEKS);
                break;
            case MONTH:
                bizTime = LocalDateUtils.plus(ldt, offset, ChronoUnit.MONTHS);
                break;
            default:
                break;
        }
        return LocalDateUtils.localDateTimeToDate(bizTime);
    }
}
