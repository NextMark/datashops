package com.bigdata.datashops.service;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.quartz.CronExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bigdata.datashops.common.Constants;
import com.bigdata.datashops.common.utils.DateUtils;
import com.bigdata.datashops.common.utils.JobUtils;
import com.bigdata.datashops.dao.mapper.JobInstanceMapper;
import com.bigdata.datashops.model.enums.JobType;
import com.bigdata.datashops.model.enums.RunState;
import com.bigdata.datashops.model.pojo.job.Job;
import com.bigdata.datashops.model.pojo.job.JobInstance;
import com.bigdata.datashops.service.utils.CronHelper;

@Service
public class JobInstanceService {
    @Autowired
    private JobService jobService;

    @Autowired
    private JobInstanceService jobInstanceService;

    @Autowired
    private JobInstanceMapper jobInstanceMapper;

    public void save(JobInstance jobInstance) {
        if(jobInstanceMapper.selectById(jobInstance.getId()) != null) {
            jobInstanceMapper.updateById(jobInstance);
        } else {
            jobInstanceMapper.insert(jobInstance);
        }
    }

    public JobInstance findById(int id) {
        return jobInstanceMapper.selectById(id);
    }

    public List<JobInstance> findRunningYarnApps(int state) {
        LambdaQueryWrapper<JobInstance> lqw = Wrappers.lambdaQuery();
        lqw.ne(JobInstance::getAppId, "");
        lqw.eq(JobInstance::getState, state);
        return jobInstanceMapper.selectList(lqw);
    }

    public List<JobInstance> findByStates(List<Integer> states) {
        LambdaQueryWrapper<JobInstance> lqw = Wrappers.lambdaQuery();
        lqw.in(JobInstance::getState, states);
        return jobInstanceMapper.selectList(lqw);
    }

    public JobInstance findByInstanceId(String instanceId) {
        LambdaQueryWrapper<JobInstance> lqw = Wrappers.lambdaQuery();
        lqw.eq(JobInstance::getInstanceId, instanceId);
        return jobInstanceMapper.selectOne(lqw);
    }

    public JobInstance findByJobIdAndBizTime(String jobId, Date bizTime) {
        LambdaQueryWrapper<JobInstance> lqw = Wrappers.lambdaQuery();
        lqw.eq(JobInstance::getJobId, jobId);
        lqw.eq(JobInstance::getBizTime, bizTime);
        return jobInstanceMapper.selectOne(lqw);
    }

    public JobInstance findByMaskIdAndBizTime(String maskId, Date bizTime) {
        LambdaQueryWrapper<JobInstance> lqw = Wrappers.lambdaQuery();
        lqw.eq(JobInstance::getMaskId, maskId);
        lqw.eq(JobInstance::getBizTime, bizTime);
        return jobInstanceMapper.selectOne(lqw);
    }

    public IPage<JobInstance> findByNameAndOperator(int pageNum, int pageSize, String name, String operator) {
        Page<JobInstance> page = new Page(pageNum, pageSize);
        return jobInstanceMapper.findJobInstanceListPaging(page, name, operator);
    }

    public void fillJob(List<JobInstance> jobInstances) {
        for (JobInstance jobInstance : jobInstances) {
            Job job = jobService.getJobByMaskIdAndVersion(jobInstance.getMaskId(), jobInstance.getVersion());
            jobInstance.setJob(job);
        }
    }

    public JobInstance createNewJobInstance(String id, String operator, Job job) {
        if (job.getType() == JobType.FLINK.getCode() || job.getType() == JobType.KAFKA_2_HDFS.getCode()) {
            return createNewJobInstance(id, operator, job, new Date());
        }
        return createNewJobInstance(id, operator, job, CronHelper.getLastTime(job.getCronExpression()));
    }

    public JobInstance createNewJobInstance(String id, String operator, Job job, Date bizDate) {
        Date now = new Date();
        String instanceId = JobUtils.genJobInstanceId();
        //Date bizDate = CronHelper.getLastTime(job.getCronExpression());
        JobInstance jobInstance = jobInstanceService.findByMaskIdAndBizTime(id, bizDate);
        if (!Objects.isNull(jobInstance)) {
            jobInstance.setOperator(operator);
            jobInstance.setSubmitTime(now);
            jobInstance.setState(RunState.CREATED.getCode());
            return jobInstance;
        }
        return JobInstance.builder().maskId(job.getMaskId()).instanceId(instanceId).submitTime(now).status(1).maskId(id)
                       .projectId(job.getProjectId()).state(RunState.CREATED.getCode()).type(job.getType())
                       .operator(operator).bizTime(bizDate).build();
    }

    public void buildBatchJobInstance(String id, String startTime, String endTime, String operator)
            throws ParseException {
        Date start = DateUtils.parse(startTime, Constants.YYYYMMDDHH);
        Date end = DateUtils.parse(endTime, Constants.YYYYMMDDHH);
        Job job = jobService.getOnlineJobByMaskId(id);
        CronExpression expression = new CronExpression(job.getCronExpression());
        for (; start.getTime() <= end.getTime(); ) {
            start = expression.getNextValidTimeAfter(start);
            if (start.getTime() >= end.getTime()) {
                break;
            }
            JobInstance jobInstance = createNewJobInstance(id, operator, job, start);
            save(jobInstance);
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
            //System.out.println(start);
            if (start.getTime() >= end.getTime()) {
                break;
            }
        }
        try {
            Date date = DateUtils.stringToDate("2021-04-09 10:15:00");
            System.out.println(CronHelper.getOffsetTriggerTime("00 */5 13-14 * * ?", date, 0));
            System.out.println(CronHelper.getOffsetTriggerTime("00 */5 10-14 * * ?", date, 0));

            System.out.println(CronHelper.getOffsetTriggerTime("00 */5 13-14 * * ?", date, -2));

            System.out.println(CronHelper.getOffsetTriggerTime("00 */5 13-14 * * ?", date, -14));
            System.out.println(CronHelper.getOffsetTriggerTime("00 10 23 * * ?", date, -2));
            System.out.println(CronHelper.getOffsetTriggerTime("00 */10 04-23 * * ?", date, 0));
            System.out.println(CronHelper.getOffsetTriggerTime("00 */10 04-23 * * ?", date, 3));

            //            Date nextValidTime = expression.getNextValidTimeAfter(new Date());
            //            Date subsequentNextValidTime = expression.getNextValidTimeAfter(nextValidTime);
            //            System.out.println(expression.getTimeBefore(new Date()));
            //            System.out.println(expression.getTimeAfter(new Date()));
            //            System.out.println(expression.getExpressionSummary());
            //
            //            long interval = subsequentNextValidTime.getTime() - nextValidTime.getTime();
            //            System.out.println(new Date(nextValidTime.getTime() - interval));
        } catch (Exception e) {
            throw new IllegalArgumentException("Unsupported cron or date", e);
        }
    }

}
