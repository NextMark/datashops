package com.bigdata.datashops.server.quartz;

import java.util.Date;
import java.util.Properties;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class QuartzService {
    private Scheduler scheduler;

    private Properties setQuartzProperties() {
        log.info("start init quartz properties");
        Properties prop = new Properties();
        prop.put("org.quartz.scheduler.instanceName", "quartzSchedulerPool");
        prop.put("org.quartz.scheduler.rmi.export", "false");
        prop.put("org.quartz.scheduler.rmi.proxy", "false");
        prop.put("org.quartz.scheduler.wrapJobExecutionInUserTransaction", "false");
        prop.put("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
        prop.put("org.quartz.threadPool.threadCount", 8);
        prop.put("org.quartz.threadPool.threadPriority", "5");
        prop.put("org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread", "true");
        prop.put("org.quartz.jobStore.misfireThreshold", "60000");
        prop.put("org.quartz.jobStore.class", "org.quartz.simpl.RAMJobStore");
        return prop;
    }

    public void start() {
        log.info("start init quartz schedule");
        try {
            SchedulerFactory schedulerFactory = new StdSchedulerFactory(setQuartzProperties());
            scheduler = schedulerFactory.getScheduler();
            scheduler.start();
            log.info("start init quartz scheduler");
        } catch (SchedulerException e) {
            log.error("failed init quartz scheduler", e);
        }
    }


    public void shutdown() {
        if (scheduler != null) {
            try {
                scheduler.shutdown();
                log.info("worker shutdown quartz service");
            } catch (SchedulerException e) {
                log.error("failed shutdown quartz scheduler", e);
            }
        }
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public void deleteJob(String name, String group) throws SchedulerException {
        JobKey jobKey = new JobKey(name, group);
        if (scheduler.checkExists(jobKey)) {
            scheduler.deleteJob(jobKey);
        }
    }

    public void scheduleCornJob(Class<? extends Job> jobClass, String name, String group, String cronExpression, JobDataMap jobDataMap, Date startDate, Date endDate) throws SchedulerException {
        JobKey jobKey = new JobKey(name, group);
        if (!scheduler.checkExists(jobKey)) {
            JobBuilder jobBuilder = JobBuilder.newJob(jobClass);
            jobBuilder.withIdentity(jobKey);
            if (jobDataMap != null && !jobDataMap.isEmpty()) {
                jobBuilder.setJobData(jobDataMap);
            }
            JobDetail jobDetail = jobBuilder.build();
            CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression).withMisfireHandlingInstructionDoNothing();
            TriggerBuilder<CronTrigger> triggerBuilder = TriggerBuilder.newTrigger().withSchedule(cronScheduleBuilder);
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

}
