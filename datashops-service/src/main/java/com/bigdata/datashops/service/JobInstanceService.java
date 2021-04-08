package com.bigdata.datashops.service;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.quartz.CronExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.bigdata.datashops.common.utils.DateUtils;
import com.bigdata.datashops.common.utils.JobUtils;
import com.bigdata.datashops.dao.data.domain.PageRequest;
import com.bigdata.datashops.dao.data.service.AbstractMysqlPagingAndSortingQueryService;
import com.bigdata.datashops.model.enums.RunState;
import com.bigdata.datashops.model.pojo.job.Job;
import com.bigdata.datashops.model.pojo.job.JobInstance;
import com.bigdata.datashops.service.utils.CronHelper;

@Service
public class JobInstanceService extends AbstractMysqlPagingAndSortingQueryService<JobInstance, Integer> {
    @Autowired
    private JobService jobService;

    @Autowired
    private JobInstanceService jobInstanceService;

    public List<JobInstance> findReadyJob(String filters) {
        return findByQuery(filters);
    }

    public JobInstance findJobInstance(String filter) {
        return findOneByQuery(filter);
    }

    public void saveEntity(JobInstance jobInstance) {
        save(jobInstance);
    }

    public Page<JobInstance> getJobInstanceList(PageRequest pageRequest) {
        return pageByQuery(pageRequest);
    }

    public void fillJob(List<JobInstance> jobInstances) {
        for (JobInstance jobInstance : jobInstances) {
            Job job = jobService.getJobByMaskId(jobInstance.getMaskId());
            jobInstance.setJob(job);
        }
    }

    public JobInstance createNewJobInstance(Integer id, String operator, Job job) {
        Date now = new Date();
        String instanceId = JobUtils.genJobInstanceId();
        Date bizDate = CronHelper.getLastTime(job.getCronExpression());
        JobInstance jobInstance = jobInstanceService.findJobInstance(String.format("jobId=%d;bizTime=%s", id, bizDate));
        if (!Objects.isNull(jobInstance)) {
            jobInstance.setOperator(operator);
            jobInstance.setSubmitTime(now);
            jobInstance.setState(RunState.CREATED.getCode());
            return jobInstance;
        }
        return JobInstance.builder().maskId(job.getMaskId()).instanceId(instanceId).submitTime(now).status(1).jobId(id)
                       .name(job.getName()).projectId(job.getProjectId()).state(RunState.CREATED.getCode())
                       .type(job.getType()).operator(operator).bizTime(bizDate).build();
    }

    public void buildBatchJobInstance(Integer id, String startTime, String endTime, String operator)
            throws ParseException {
        Date start = DateUtils.parse(startTime, "yyyyMMddHH");
        Date end = DateUtils.parse(endTime, "yyyyMMddHH");
        Job job = jobService.getJob(id);
        CronExpression expression = new CronExpression(job.getCronExpression());
        for (; start.getTime() <= end.getTime(); ) {
            start = expression.getNextValidTimeAfter(start);
            if (start.getTime() >= end.getTime()) {
                break;
            }
            JobInstance jobInstance = createNewJobInstance(id, operator, job);
            saveEntity(jobInstance);
        }
    }

    public static void main(String[] args) throws ParseException {
        String startTime = "2021040111";
        String endTime = "2021040112";
        Date start = DateUtils.parse(startTime, "yyyyMMddHH");
        Date end = DateUtils.parse(endTime, "yyyyMMddHH");
        CronExpression expression = new CronExpression("00 */10 04-23 * * ?");
        for (; start.getTime() <= end.getTime(); ) {
            start = expression.getNextValidTimeAfter(start);
            System.out.println(start);
            if (start.getTime() >= end.getTime()) {
                break;
            }
        }
    }

}
