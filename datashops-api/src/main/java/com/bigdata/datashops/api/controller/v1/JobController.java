package com.bigdata.datashops.api.controller.v1;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.internal.guava.Sets;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedPseudograph;
import org.quartz.SchedulerException;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bigdata.datashops.api.common.Pagination;
import com.bigdata.datashops.api.controller.BasicController;
import com.bigdata.datashops.api.response.Result;
import com.bigdata.datashops.api.response.ResultCode;
import com.bigdata.datashops.api.utils.ValidatorUtil;
import com.bigdata.datashops.common.Constants;
import com.bigdata.datashops.common.utils.DateUtils;
import com.bigdata.datashops.common.utils.JSONUtils;
import com.bigdata.datashops.common.utils.JobUtils;
import com.bigdata.datashops.model.dto.DtoCronExpression;
import com.bigdata.datashops.model.dto.DtoJobGraph;
import com.bigdata.datashops.model.dto.DtoPageQuery;
import com.bigdata.datashops.model.enums.JobType;
import com.bigdata.datashops.model.enums.RunState;
import com.bigdata.datashops.model.enums.SchedulingPeriod;
import com.bigdata.datashops.model.pojo.job.Edge;
import com.bigdata.datashops.model.pojo.job.Job;
import com.bigdata.datashops.model.pojo.job.JobGraph;
import com.bigdata.datashops.model.pojo.job.JobInstance;
import com.bigdata.datashops.model.pojo.job.JobRelation;
import com.bigdata.datashops.service.graph.Vertex;
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
    public Result modifyJob(@RequestBody Job dtoJob) {
        Job job = jobService.getOnlineJobByMaskId(dtoJob.getMaskId());

        //BeanUtils.copyProperties(dtoJob, job, "data");
        DtoCronExpression cronExpression = DtoCronExpression.builder().schedulingPeriod(dtoJob.getSchedulingPeriod())
                                                   .config(dtoJob.getTimeConfig()).build();
        String cron = CronHelper.buildCronExpression(cronExpression);
        dtoJob.setCronExpression(cron);
        if (StringUtils.isNotEmpty(dtoJob.getData())) {
            dtoJob.setData(JobUtils.buildJobData(dtoJob.getType(), dtoJob.getData()));
        }
        if (!job.equals(dtoJob)) {
            dtoJob.setId(null);
            dtoJob.setVersion(job.getVersion() + 1);
            jobService.updateStatusOffline(job.getMaskId(), job.getVersion());
            jobService.save(dtoJob);
            return ok(dtoJob);
        } else {
            return ok(job);
        }

        //        BeanUtils.copyProperties(dtoJob, job);
        //        DtoCronExpression cronExpression = DtoCronExpression.builder().schedulingPeriod(dtoJob
        //        .getSchedulingPeriod())
        //                                                   .config(dtoJob.getTimeConfig()).build();
        //        String cron = CronHelper.buildCronExpression(cronExpression);
        //        job.setCronExpression(cron);
        //        if (StringUtils.isNotEmpty(dtoJob.getData())) {
        //            job.setData(JobUtils.buildJobData(dtoJob.getType(), dtoJob.getData()));
        //        }
        //        jobService.modifyJob(job);
        //        return ok(job);
    }

    @PostMapping(value = "/addNewJob")
    public Result addNewJob(@RequestBody Map<String, String> params) {
        String name = params.get("name");
        Job job = new Job();
        job.setRetry(0);
        job.setNotifyType(1);
        job.setTimeout(86400);
        job.setOwner(params.get("owner"));
        job.setProjectId(Integer.valueOf(params.get("projectId")));
        job.setMaskId(JobUtils.genMaskId("1-" + params.get("projectId") + "-"));
        job.setHostSelector(Integer.valueOf(params.get("hostSelector")));
        job.setName(name);
        job.setCronExpression("00 10 12 * * ?");
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
        IPage<JobGraph> jobs =
                jobGraphService.findList(query.getPageNum(), query.getPageSize(), query.getName(), query.getOwner());
        Pagination pagination = new Pagination(jobs);
        return ok(pagination);
    }

    @PostMapping(value = "/getJobList")
    public Result getJobList(@RequestBody DtoPageQuery query) {
        IPage<Job> jobs = jobService.findByNameAndOwner(query.getPageNum(), query.getPageSize(), query.getName(),
                query.getOwner());

        Pagination pagination = new Pagination(jobs);
        return ok(pagination);
    }

    @RequestMapping(value = "/getJobByMaskId")
    public Result getJobByMaskId(@NotNull String maskId) {
        Job job = jobService.getOnlineJobByMaskId(maskId);
        return ok(job);
    }

    @RequestMapping(value = "/getVersionList")
    public Result getVersionList(@NotNull String maskId) {
        List<Job> res = jobService.getVersionList(maskId);
        return ok(res);
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
        Job job = jobService.getOnlineJobByMaskId(params.get("maskId").toString());
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
        JobRelation jobRelation = jobRelationService.findByQuery(graphId, jobMaskId, jobIdStr[1]);
        jobRelation.setTopPos(params.get("top"));
        jobRelation.setLeftPos(params.get("left"));
        jobRelationService.save(jobRelation);
        return ok();
    }

    @RequestMapping(value = "/getJobGraph")
    public Result getJobGraph(@NotNull String id) {
        DirectedWeightedPseudograph<String, DefaultWeightedEdge> dag =
                new DirectedWeightedPseudograph<>(DefaultWeightedEdge.class);
        Set<Edge> edges = Sets.newHashSet();
        Set<String> vertexSet = Sets.newHashSet();
        graphService.buildGraph(dag, edges, vertexSet, id);
        Map<String, Object> res = Maps.newHashMap();
        Set<Vertex> vertices = Sets.newHashSet();
        for (String s : dag.vertexSet()) {
            Job job = jobService.getOnlineJobByMaskId(s);
            Map<String, Object> extra = Maps.newHashMap();
            extra.put("period", job.getSchedulingPeriod());
            extra.put("owner", job.getOwner());
            Vertex vertex = new Vertex(s, job.getName(), job.getType(), JSONUtils.toJsonString(extra));
            vertices.add(vertex);
        }
        res.put("edges", edges);
        res.put("nodes", vertices);
        return ok(res);
    }

    @RequestMapping(value = "/batchRunJob")
    public Result batchRunJob(@NotNull String id, @NotNull String operator, String startTime, String endTime)
            throws ParseException {
        jobInstanceService.buildBatchJobInstance(id, startTime, endTime, operator);
        return ok();
    }

    @RequestMapping(value = "/runJob")
    public Result runJob(@NotNull String id, @NotNull String operator) {
        Job job = jobService.getOnlineJobByMaskId(id);
        JobInstance instance = jobInstanceService.createNewJobInstance(id, operator, job);
        jobInstanceService.save(instance);
        return ok();
    }

    @RequestMapping(value = "/reRunJob")
    public Result reRunJob(@NotNull Integer id, @NotNull String operator) {
        JobInstance instance = jobInstanceService.findById(id);
        instance.setState(RunState.CREATED.getCode());
        instance.setOperator(operator);
        instance.setSubmitTime(new Date());
        instance.setEndTime(null);
        jobInstanceService.save(instance);
        return ok();
    }

    @RequestMapping(value = "/cancelJob")
    public Result cancelJob(@NotNull Integer id, @NotNull String operator) {
        JobInstance instance = jobInstanceService.findById(id);
        if (JobType.yarnJobType().contains(instance.getType())) {
            //hadoopService.cancelApplication(instance.getAppId());
        }
        instance.setState(RunState.CANCEL.getCode());
        instance.setOperator(operator);
        instance.setSubmitTime(new Date());
        instance.setEndTime(null);
        jobInstanceService.save(instance);
        return ok();
    }

    @RequestMapping(value = "/backToVersion")
    public Result backToVersion(@NotNull Integer version, @NotNull String maskId) {
        jobService.backToHistoryVersion(maskId, version);
        return ok();
    }

    @RequestMapping(value = "/getGraph")
    public Result getGraph(@NotNull String maskId) {
        DirectedWeightedPseudograph<String, DefaultWeightedEdge> dag =
                new DirectedWeightedPseudograph<>(DefaultWeightedEdge.class);
        Set<Edge> dependencyList = Sets.newHashSet();
        Set<String> vertexSet = Sets.newHashSet();
        graphService.buildGraph(dag, dependencyList, vertexSet, maskId);
        Map<String, Object> res = Maps.newHashMap();
        res.put("edge", dependencyList);
        res.put("vertex", vertexSet);
        return ok(res);
    }
}
