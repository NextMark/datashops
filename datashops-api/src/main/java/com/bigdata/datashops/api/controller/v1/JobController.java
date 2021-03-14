package com.bigdata.datashops.api.controller.v1;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.quartz.SchedulerException;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bigdata.datashops.api.common.Pagination;
import com.bigdata.datashops.api.controller.BasicController;
import com.bigdata.datashops.api.response.Result;
import com.bigdata.datashops.api.response.ResultCode;
import com.bigdata.datashops.api.utils.ValidatorUtil;
import com.bigdata.datashops.common.Constants;
import com.bigdata.datashops.common.utils.DateUtils;
import com.bigdata.datashops.common.utils.JSONUtils;
import com.bigdata.datashops.common.utils.JobUtils;
import com.bigdata.datashops.dao.data.domain.PageRequest;
import com.bigdata.datashops.model.DtoJobGraph;
import com.bigdata.datashops.model.dto.DtoCronExpression;
import com.bigdata.datashops.model.dto.DtoPageQuery;
import com.bigdata.datashops.model.enums.JobType;
import com.bigdata.datashops.model.enums.SchedulingPeriod;
import com.bigdata.datashops.model.pojo.job.Job;
import com.bigdata.datashops.model.pojo.job.JobDependency;
import com.bigdata.datashops.model.pojo.job.JobGraph;
import com.bigdata.datashops.model.pojo.job.JobRelation;
import com.bigdata.datashops.service.utils.CronHelper;
import com.google.common.collect.Maps;

@RestController
@RequestMapping("/v1/job")
public class JobController extends BasicController {
    @RequestMapping(value = "/getJobGraphById")
    public Result getJobGraphById(@NotNull Integer id) {
        JobGraph jobGraph = jobGraphService.getJobGraph(id);
        jobGraphService.fillJobWithDependency(jobGraph);
        return ok(jobGraph);
    }

    @RequestMapping(value = "/getJobGraphByMaskId")
    public Result getJobGraphByMaskId(@NotNull String id) {
        JobGraph jobGraph = jobGraphService.getJobGraphByMaskId(id);
        jobGraphService.fillJobWithDependency(jobGraph);
        return ok(jobGraph);
    }

    @PostMapping(value = "/modifyJobGraph")
    public Result modifyJobGraph(@RequestBody DtoJobGraph dtoJobGraph) {
        ValidatorUtil.validate(dtoJobGraph);
        JobGraph jobGraph = new JobGraph();
        jobGraph.setStatus(1);
        BeanUtils.copyProperties(dtoJobGraph, jobGraph);
        jobGraphService.save(jobGraph);
        return ok();
    }

    @PostMapping(value = "/modifyJob")
    public Result modifyJob(@RequestBody Job dtoJob) throws SchedulerException {
        Job job = jobService.getJob(dtoJob.getId());
        BeanUtils.copyProperties(dtoJob, job);
        DtoCronExpression cronExpression = DtoCronExpression.builder().schedulingPeriod(dtoJob.getSchedulingPeriod())
                                                   .config(dtoJob.getTimeConfig()).build();
        String cron = CronHelper.buildCronExpression(cronExpression);
        job.setCronExpression(cron);
        if (dtoJob.getData() != null) {
            job.setData(JobUtils.buildJobData(dtoJob.getType(), dtoJob.getData()));
        }
        jobService.modifyJob(job);
        return ok(job);
    }

    @PostMapping(value = "/saveHiveSql")
    public Result saveHiveSql(@RequestBody Map<String, String> params) {
        String maskId = params.get("maskId");
        Job job = jobService.getJobByMaskId(maskId);
        if (Objects.isNull(job)) {
            job = new Job();
        }
        job.setData(JSONUtils.toJsonString(JobUtils.buildJobData(0, params.get("sql"))));
        jobService.save(job);
        return ok();
    }

    @PostMapping(value = "/addNewJob")
    public Result addNewJob(@RequestBody Map<String, String> params) {
        String name = params.get("name");
        Job job = new Job();
        job.setOwner(params.get("owner"));
        job.setProjectId(Integer.valueOf(params.get("projectId")));
        job.setMaskId(JobUtils.genMaskId("1-" + params.get("projectId") + "-"));
        job.setHostSelector(Integer.valueOf(params.get("hostSelector")));
        job.setName(name);
        // 初始化调度时间
        job.setSchedulingPeriod(SchedulingPeriod.DAY.getCode());
        Map<String, String> timeConfig = Maps.newHashMap();
        timeConfig.put("hour", DateUtils.getCurrentTime("HH"));
        timeConfig.put("minute", DateUtils.getCurrentTime("mm"));
        job.setTimeConfig(JSONUtils.toJsonString(timeConfig));

        job.setType(Integer.valueOf(params.get("type")));
        jobService.save(job);
        return ok(job);
    }

    @PostMapping(value = "/getJobGraphList")
    public Result getJobGraphList(@RequestBody DtoPageQuery query) {
        StringBuilder filter = new StringBuilder("status=1");
        if (StringUtils.isNoneBlank(query.getName())) {
            filter.append(";name?").append(query.getName());
        }
        if (StringUtils.isNoneBlank(query.getOwner())) {
            filter.append(";owner?").append(query.getOwner());
        }
        PageRequest pageRequest =
                new PageRequest(query.getPageNum() - 1, query.getPageSize(), filter.toString(), Sort.Direction.DESC,
                        "createTime");
        Page<JobGraph> jobGraphs = jobGraphService.getJobGraphList(pageRequest);
        Pagination pagination = new Pagination(jobGraphs);
        return ok(pagination);
    }

    @PostMapping(value = "/getJobList")
    public Result getJobList(@RequestBody DtoPageQuery query) {
        StringBuilder filter = new StringBuilder("status=1");
        if (StringUtils.isNoneBlank(query.getName())) {
            filter.append(";name?").append(query.getName());
        }
        if (StringUtils.isNoneBlank(query.getOwner())) {
            filter.append(";owner?").append(query.getOwner());
        }
        PageRequest pageRequest =
                new PageRequest(query.getPageNum() - 1, query.getPageSize(), filter.toString(), Sort.Direction.DESC,
                        "createTime");
        Page<Job> jobGraphs = jobService.getJobList(pageRequest);
        Pagination pagination = new Pagination(jobGraphs);
        return ok(pagination);
    }

    @RequestMapping(value = "/getJobByMaskId")
    public Result getJobByMaskId(@NotNull String maskId) {
        Job job = jobService.getJobByMaskId(maskId);
        return ok(job);
    }

    @PostMapping(value = "/deleteJob")
    public Result deleteJob(@RequestBody Map<String, String> params) {
        String graphMaskId = params.get("graphMaskId");
        String jobMaskId = params.get("jobMaskId");
        jobRelationService.delete(graphMaskId, jobMaskId);
        return ok();
    }

    @PostMapping(value = "/deleteJobGraph")
    public Result deleteJobGraph(@RequestBody Map<String, Integer> id) {
        JobGraph jobGraph = jobGraphService.getJobGraph(id.get("id"));
        if (!Objects.isNull(jobGraph)) {
            jobGraph.setStatus(0);
            jobGraphService.save(jobGraph);
        }
        return ok();
    }

    @PostMapping(value = "/modifySchedulerStatus")
    public Result modifySchedulerStatus(@RequestBody Map<String, Object> params) throws SchedulerException {
        int status = (int) params.get("status");
        Job job = jobService.getJobByMaskId(params.get("maskId").toString());
        if (!Objects.isNull(job)) {
            jobService.modifySchedulerStatus(job, status);
        }
        return ok();
    }

    @PostMapping(value = "/addJobToGraph")
    public Result addJobToGraph(@RequestBody Map<String, String> params) {
        String graphId = params.get("graphMaskId");
        String jobId = params.get("jobMaskId");
        int type = Integer.parseInt(params.get("type"));
        JobGraph jobGraph = jobGraphService.getJobGraphByMaskId(graphId);
        if (Objects.isNull(jobGraph)) {
            return fail(ResultCode.FAILURE);
        }
        JobRelation jobRelation = new JobRelation();
        jobRelation.setTopPos("0px");
        jobRelation.setLeftPos("0px");
        jobRelation.setGraphMaskId(graphId);
        jobRelation.setJobMaskId(jobId);
        jobRelation.setNodeType(type);
        jobRelationService.save(jobRelation);
        return ok();
    }

    @PostMapping(value = "/addNewJobToGraph")
    public Result addNewJobToGraph(@RequestBody Map<String, String> params) {
        String graphId = params.get("graphMaskId");
        JobType jobType = JobType.valueOf(JobType.class, params.get("type").toUpperCase());
        String name = String.valueOf(params.get("name"));
        String owner = String.valueOf(params.get("owner"));
        String ico = params.get("ico");
        Job job = new Job();
        // todo project id
        job.setMaskId(JobUtils.genMaskId("1-" + "1-"));
        job.setType(jobType.getCode());
        job.setOwner(owner);
        job.setName(name);
        job.setIco(ico);
        job.setStatus(1);
        job = jobService.save(job);

        JobRelation jobRelation = new JobRelation();
        jobRelation.setGraphMaskId(graphId);
        jobRelation.setJobMaskId(job.getMaskId());
        jobRelation.setNodeType(1);
        jobRelation.setLeftPos(params.get("left"));
        jobRelation.setTopPos(params.get("top"));
        jobRelationService.save(jobRelation);
        return ok(job);
    }

    @PostMapping(value = "/modifyPosition")
    public Result modifyPosition(@RequestBody Map<String, String> params) {
        String graphId = params.get("graphMaskId");
        String jobMaskId = params.get("jobMaskId");
        String[] jobIdStr = jobMaskId.split(Constants.SEPARATOR_HYPHEN);
        if (jobIdStr[1].contains("+")) {
            return ok();
        }
        String filter = "graphMaskId=" + graphId + ";jobMaskId=" + jobMaskId + ";nodeType=" + jobIdStr[1];
        JobRelation jobRelation = jobRelationService.findOneByQuery(filter);
        jobRelation.setTopPos(params.get("top"));
        jobRelation.setLeftPos(params.get("left"));
        jobRelationService.save(jobRelation);
        return ok();
    }

    @PostMapping(value = "/modifyOffset")
    public Result modifyOffset(@RequestBody Map<String, String> params) {
        String graphMaskId = params.get("graphMaskId");
        String sourceId = params.get("sourceId");
        String targetId = params.get("targetId");
        int offset = Integer.parseInt(params.get("offset"));
        String filter = String.format("graphMaskId=%s;sourceId=%s;targetId=%s", graphMaskId, sourceId, targetId);
        JobDependency jobDependency = jobDependencyService.getOne(filter);
        jobDependency.setOffset(offset);
        jobDependencyService.save(jobDependency);
        return ok();
    }

    @RequestMapping(value = "/getJobGraph")
    public Result getJobGraph(@NotNull Integer id) {
        Map<String, Object> nodes = jobDependencyService.getJobDependencyGraph(id);
        return ok(nodes);
    }
}
