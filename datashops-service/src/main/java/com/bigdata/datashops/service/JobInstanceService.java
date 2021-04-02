package com.bigdata.datashops.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.bigdata.datashops.common.Constants;
import com.bigdata.datashops.common.utils.JobUtils;
import com.bigdata.datashops.dao.data.domain.PageRequest;
import com.bigdata.datashops.dao.data.service.AbstractMysqlPagingAndSortingQueryService;
import com.bigdata.datashops.model.enums.RunState;
import com.bigdata.datashops.model.pojo.job.Job;
import com.bigdata.datashops.model.pojo.job.JobDependency;
import com.bigdata.datashops.model.pojo.job.JobInstance;
import com.bigdata.datashops.service.utils.JobHelper;
import com.google.common.collect.Lists;

@Service
public class JobInstanceService extends AbstractMysqlPagingAndSortingQueryService<JobInstance, Integer> {
    @Autowired
    private JobService jobService;

    @Autowired
    private JobDependencyService jobDependencyService;

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
            jobInstance.setData(job.getData());
            jobInstance.setJob(job);
        }
    }

    public JobInstance createNewJobInstance(Integer id, String operator) {
        Job job = jobService.getJob(id);
        Date now = new Date();
        String instanceId = JobUtils.genJobInstanceId();
        Date bizDate = JobHelper.getBizDate(job.getSchedulingPeriod());

        List<JobDependency> preJobs = jobDependencyService.getJobDependency("targetId=" + id);
        List<String> pre = Lists.newArrayList();
        for (JobDependency dependency : preJobs) {
            pre.add(String.format("%s|%s", id, dependency.getOffset()));
        }

        List<JobDependency> postJobs = jobDependencyService.getJobDependency("sourceId=" + id);
        List<String> post = Lists.newArrayList();
        for (JobDependency dependency : postJobs) {
            post.add(String.format("%s|%s", id, dependency.getOffset()));
        }
        return JobInstance.builder().maskId(job.getMaskId()).instanceId(instanceId).submitTime(now).status(1).jobId(id)
                       .host(job.getHost()).hostSelector(job.getHostSelector()).data(job.getData()).name(job.getName())
                       .priority(job.getPriority()).projectId(job.getProjectId()).retryTimes(job.getRetryTimes())
                       .retryInterval(job.getRetryInterval()).state(RunState.CREATED.getCode()).type(job.getType())
                       .preDependency(StringUtils.join(pre, Constants.SEPARATOR_COMMA))
                       .postDependency(StringUtils.join(post, Constants.SEPARATOR_COMMA)).operator(operator)
                       .bizTime(bizDate).priority(job.getPriority()).build();
    }

}
