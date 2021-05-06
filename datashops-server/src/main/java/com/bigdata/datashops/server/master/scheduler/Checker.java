package com.bigdata.datashops.server.master.scheduler;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.compress.utils.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bigdata.datashops.common.Constants;
import com.bigdata.datashops.common.utils.LocalDateUtils;
import com.bigdata.datashops.model.enums.RunState;
import com.bigdata.datashops.model.enums.SchedulingPeriod;
import com.bigdata.datashops.model.pojo.job.Job;
import com.bigdata.datashops.model.pojo.job.JobDependency;
import com.bigdata.datashops.model.pojo.job.JobInstance;
import com.bigdata.datashops.service.JobDependencyService;
import com.bigdata.datashops.service.JobInstanceService;
import com.bigdata.datashops.service.JobService;
import com.bigdata.datashops.service.utils.CronHelper;

@Service
public class Checker {
    private static final Logger LOG = LoggerFactory.getLogger(Checker.class);

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
            int type = s.getType();
            String offset = s.getOffset();
            String[] offsetRegion = offset.split(Constants.SEPARATOR_COMMA);
            List<Integer> offsets = Lists.newArrayList();
            if (type == 1) {
                offsets = Arrays.stream(offsetRegion).map(Integer::valueOf).collect(Collectors.toList());
            } else if (type == 2) {
                int begin = Integer.parseInt(offsetRegion[0]);
                int end = Integer.parseInt(offsetRegion[1]);
                if (begin > end) {
                    return RunState.WAIT_FOR_DEPENDENCY;
                }
                for (int i = begin; i < end; i++) {
                    offsets.add(i);
                }
            } else {
                return RunState.SUCCESS;
            }

            List<Date> dependencyBizTime;
            String filter;
            Job preJob = jobService.getJob(preJobId);
            for (Integer o : offsets) {
                int schedulingPeriod = preJob.getSchedulingPeriod();
                dependencyBizTime = getDependencyBizTime(bizTime, schedulingPeriod, o, preJob.getCronExpression());
                for (Date date : dependencyBizTime) {
                    filter = String.format("jobId=%s;bizTime=%s;", preJobId, date);
                    JobInstance instance = jobInstanceService.findJobInstance(filter);
                    if (instance == null) {
                        return RunState.WAIT_FOR_DEPENDENCY;
                    }
                    RunState runState = RunState.of(instance.getState());
                    if (runState != RunState.SUCCESS) {
                        LOG.info("Dependency not ready, instanceId={}, preJobId={}, offset={}, bizTime={}",
                                instance.getInstanceId(), preJobId, offset, instance.getBizTime());
                        return RunState.WAIT_FOR_DEPENDENCY;
                    }
                }
            }
        }
        return RunState.SUCCESS;
    }

    private List<Date> getDependencyBizTime(Date date, int schedulingPeriod, int offset, String weekOrMonthStr) {
        List<Date> result = Lists.newArrayList();
        LocalDateTime ldt = LocalDateUtils.dateToLocalDateTime(date);
        LocalDateTime bizTime;
        switch (SchedulingPeriod.of(schedulingPeriod)) {
            case MINUTE:
            case HOUR:
            case DAY:
                result.add(CronHelper.getOffsetTriggerTime(weekOrMonthStr, date, offset));
                break;
            case WEEK:
                bizTime = LocalDateUtils.plus(ldt, offset, ChronoUnit.WEEKS);
                String[] weeks =
                        weekOrMonthStr.split(Constants.SEPARATOR_WHITE_SPACE)[5].split(Constants.SEPARATOR_COMMA);
                for (String week : weeks) {
                    result.add(LocalDateUtils.parseStringToDate(
                            LocalDateUtils.getDateOfWeekStr(bizTime, Integer.parseInt(week))));
                }
                break;
            case MONTH:
                bizTime = LocalDateUtils.plus(ldt, offset, ChronoUnit.MONTHS);
                String[] months =
                        weekOrMonthStr.split(Constants.SEPARATOR_WHITE_SPACE)[3].split(Constants.SEPARATOR_COMMA);
                for (String month : months) {
                    result.add(LocalDateUtils.parseStringToDate(
                            LocalDateUtils.getDateOfMonthStr(bizTime, Integer.parseInt(month))));
                }
                break;
            default:
                break;
        }
        return result;
    }
}
