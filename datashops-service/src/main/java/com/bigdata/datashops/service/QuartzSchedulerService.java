package com.bigdata.datashops.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bigdata.datashops.model.pojo.job.Job;
import com.bigdata.datashops.service.quartz.QuartzJob;

@Service
public class QuartzSchedulerService {
    @Autowired
    @Qualifier("schedulerFactoryBean")
    private Scheduler scheduler;

    public void addJobScheduler(Job job) throws SchedulerException {
        Integer jobId = job.getId();
        Integer projectId = job.getProjectId();
        Date startDate = job.getValidStartDate();
        Date endDate = job.getValidEndDate();
        Map<String, Object> dataMap = buildDataMap(projectId, jobId);
        scheduleCronJob(QuartzJob.class, jobId.toString(), projectId.toString(), startDate, endDate,
                job.getCronExpression(), dataMap);
    }

    @Transactional
    public void rescheduleJob(com.bigdata.datashops.model.pojo.job.Job job) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(job.getId().toString(), job.getProjectId().toString());
        if (scheduler.checkExists(triggerKey)) {
            CronTrigger cronTrigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            String oldCronExpression = cronTrigger.getCronExpression();
            if (!StringUtils.equalsIgnoreCase(job.getCronExpression(), oldCronExpression)) {
                CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression());
                cronTrigger = cronTrigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(cronScheduleBuilder)
                                      .build();
                scheduler.rescheduleJob(triggerKey, cronTrigger);
            }
        }
    }

    @Transactional
    public void deleteJob(Integer jobId, Integer projectId) throws SchedulerException {
        JobKey jobKey = new JobKey(jobId.toString(), projectId.toString());
        if (scheduler.checkExists(jobKey)) {
            scheduler.deleteJob(jobKey);
        }
    }

    @Transactional
    public void pauseJob(Integer projectId, Integer jobId) throws SchedulerException {
        JobKey jobKey = new JobKey(jobId.toString(), projectId.toString());
        if (scheduler.checkExists(jobKey)) {
            scheduler.pauseJob(jobKey);
        }
    }

    @Transactional
    public void resumeJob(Integer projectId, Integer jobId) throws SchedulerException {
        JobKey jobKey = new JobKey(jobId.toString(), projectId.toString());
        if (scheduler.checkExists(jobKey)) {
            scheduler.resumeJob(jobKey);
        }
    }

    @Transactional
    public void scheduleCronJob(Class<? extends org.quartz.Job> jobClass, String name, String group, Date startDate,
                                Date endDate, String cronExpression, Map<String, Object> jobDataMap)
            throws SchedulerException {
        JobKey jobKey = new JobKey(name, group);
        if (!scheduler.checkExists(jobKey)) {
            JobBuilder jobBuilder = JobBuilder.newJob(jobClass);
            jobBuilder.withIdentity(jobKey);
            JobDetail jobDetail = jobBuilder.build();
            jobDetail.getJobDataMap().putAll(jobDataMap);
            CronScheduleBuilder cronScheduleBuilder =
                    CronScheduleBuilder.cronSchedule(cronExpression).withMisfireHandlingInstructionDoNothing();
            TriggerBuilder<CronTrigger> triggerBuilder =
                    TriggerBuilder.newTrigger().withIdentity(name, group).withSchedule(cronScheduleBuilder);
            if (startDate != null) {
                triggerBuilder.startAt(startDate);
            } else {
                triggerBuilder.startNow();
            }
            if (endDate != null) {
                triggerBuilder.endAt(endDate);
            }
            CronTrigger trigger = triggerBuilder.build();
            scheduler.scheduleJob(jobDetail, trigger);
        }
    }

    private static Map<String, Object> buildDataMap(int projectId, int jobId) {
        Map<String, Object> dataMap = new HashMap<>(2);
        dataMap.put("projectId", projectId);
        dataMap.put("jobId", jobId);
        return dataMap;
    }

}
