package com.bigdata.datashops.server.master.scheduler;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bigdata.datashops.common.utils.LocalDateUtils;
import com.bigdata.datashops.model.enums.RunState;
import com.bigdata.datashops.model.enums.SchedulingPeriod;
import com.bigdata.datashops.model.pojo.job.Job;
import com.bigdata.datashops.model.pojo.job.JobDependency;
import com.bigdata.datashops.model.pojo.job.JobInstance;
import com.bigdata.datashops.service.JobDependencyService;
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

    @Autowired
    private JobDependencyService jobDependencyService;

    public RunState check(JobInstance jobInstance) {
        List<JobDependency> jobDependencies =
                jobDependencyService.getJobDependency("targetId=" + jobInstance.getJobId());
        if (jobDependencies.size() == 0) {
            return RunState.SUCCESS;
        }
        Date bizTime = jobInstance.getBizTime();
        for (JobDependency s : jobDependencies) {
            int preJobId = s.getSourceId();
            int offset = s.getOffset();
            Date dependencyBizTime;
            StringBuilder sb = new StringBuilder();
            Job preJob = jobService.getJob(preJobId);
            // TODO
            int schedulingPeriod = preJob.getSchedulingPeriod();
            dependencyBizTime = getDependencyBizTime(bizTime, schedulingPeriod, offset);
            sb.append("jobId=").append(preJobId).append(";");
            sb.append("bizTime=").append(dependencyBizTime).append(";");

            JobInstance relyOn = jobInstanceService.findJobInstance(sb.toString());
            if (relyOn == null) {
                return RunState.SUCCESS;
            }
            RunState runState = RunState.of(relyOn.getState());
            if (runState != RunState.SUCCESS) {
                LOG.info("Dependency not ready, instanceId={}, preJobId={}, offset={}, bizTime={}",
                        jobInstance.getInstanceId(), preJobId, offset, relyOn.getBizTime());
                return RunState.WAIT_FOR_DEPENDENCY;
            }
        }
        return RunState.SUCCESS;
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
