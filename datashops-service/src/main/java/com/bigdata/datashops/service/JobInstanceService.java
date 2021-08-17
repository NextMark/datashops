package com.bigdata.datashops.service;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
import com.bigdata.datashops.common.utils.JSONUtils;
import com.bigdata.datashops.common.utils.JobUtils;
import com.bigdata.datashops.dao.mapper.JobInstanceMapper;
import com.bigdata.datashops.model.enums.JobType;
import com.bigdata.datashops.model.enums.RunState;
import com.bigdata.datashops.model.pojo.job.Job;
import com.bigdata.datashops.model.pojo.job.JobDependency;
import com.bigdata.datashops.model.pojo.job.JobInstance;
import com.bigdata.datashops.service.utils.CronHelper;
import com.bigdata.datashops.service.utils.JobHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Service
public class JobInstanceService extends BaseService {

    @Autowired
    private JobInstanceMapper jobInstanceMapper;

    public void save(JobInstance jobInstance) {
        if (jobInstanceMapper.selectById(jobInstance.getId()) != null) {
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

    public JobInstance findByMaskIdAndVersionAndBizTime(String maskId, int version, String bizTime) {
        LambdaQueryWrapper<JobInstance> lqw = Wrappers.lambdaQuery();
        lqw.eq(JobInstance::getVersion, version);
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
        JobInstance instance = new JobInstance();
        instance.setMaskId(id);
        instance.setInstanceId(instanceId);
        instance.setSubmitTime(now);
        instance.setStatus(1);
        instance.setProjectId(job.getProjectId());
        instance.setState(RunState.CREATED.getCode());
        instance.setType(job.getType());
        instance.setOperator(operator);
        instance.setBizTime(bizDate);
        // 计算上下游
        fillUpstreamVertex(instance);
        fillDownstreamVertex(instance);
        return instance;
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

    public void fillUpstreamVertex(JobInstance instance) {
        List<JobDependency> dependencyList = jobDependencyService.findByTargetId(instance.getMaskId());
        List<Map> upstream = Lists.newArrayList();
        for (JobDependency dependency : dependencyList) {
            String preJobId = dependency.getSourceId();
            Job sourceJob = jobService.getOnlineJobByMaskId(preJobId);
            int type = dependency.getType();
            List<Integer> offsets = JobHelper.parseOffsetToList(dependency.getOffset(), type);
            for (Integer offset : offsets) {
                Date date = CronHelper.getUpstreamBizTime(sourceJob.getCronExpression(), instance.getBizTime(), offset);
                Map<String, Object> map = Maps.newHashMap();
                map.put("maskId", preJobId);
                map.put("version", sourceJob.getVersion());
                map.put("bizTime", date);
                upstream.add(map);
            }
        }
        instance.setUpstreamVertex(JSONUtils.toJsonString(upstream));
    }

    public void fillDownstreamVertex(JobInstance instance) {
        List<JobDependency> dependencyList = jobDependencyService.findBySourceId(instance.getMaskId());
        List<Map> downstream = Lists.newArrayList();
        for (JobDependency dependency : dependencyList) {
            String jobId = dependency.getSourceId();
            Job sourceJob = jobService.getOnlineJobByMaskId(dependency.getSourceId());
            Job targetJob = jobService.getOnlineJobByMaskId(dependency.getTargetId());
            int type = dependency.getType();
            List<Integer> offsets = JobHelper.parseOffsetToList(dependency.getOffset(), type);
            for (Integer offset : offsets) {
                List<Date> dateList = CronHelper.getDownstreamBizTime(instance.getBizTime(), offset,
                        sourceJob.getSchedulingPeriod(), sourceJob.getCronExpression(), targetJob.getCronExpression());
                for (Date date : dateList) {
                    Map<String, Object> map = Maps.newHashMap();
                    map.put("maskId", jobId);
                    map.put("version", targetJob.getVersion());
                    map.put("bizTime", date);
                    downstream.add(map);
                }
            }
        }
        instance.setDownstreamVertex(JSONUtils.toJsonString(downstream));
    }
}
