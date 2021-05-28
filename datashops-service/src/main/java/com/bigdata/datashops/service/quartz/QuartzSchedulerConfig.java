package com.bigdata.datashops.service.quartz;

import java.io.IOException;
import java.util.Properties;

import org.quartz.Scheduler;
import org.quartz.spi.JobFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Configuration
@EnableScheduling
public class QuartzSchedulerConfig {

    @Autowired
    protected MyJobFactory myJobFactory;

    @Bean
    public JobFactory jobFactory(ApplicationContext applicationContext) {
        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() throws IOException {
        SchedulerFactoryBean scheduler = new SchedulerFactoryBean();
        scheduler.setQuartzProperties(quartzProperties());
        boolean autoStartUp = Boolean.parseBoolean(quartzProperties().getProperty("quartz.autoStartup"));
        scheduler.setAutoStartup(autoStartUp);
        scheduler.setJobFactory(myJobFactory);
        return scheduler;
    }

    @Bean
    public Scheduler scheduler() throws Exception {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setOverwriteExistingJobs(true);
        factory.setJobFactory(myJobFactory);

        factory.setQuartzProperties(quartzProperties());
        factory.afterPropertiesSet();
        Scheduler scheduler = schedulerFactoryBean().getScheduler();
        scheduler.setJobFactory(myJobFactory);
        return scheduler;
    }

    @Bean
    public Properties quartzProperties() throws IOException {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
        propertiesFactoryBean.afterPropertiesSet();
        return propertiesFactoryBean.getObject();
    }

}
