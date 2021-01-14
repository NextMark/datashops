package com.bigdata.datashops.api.controller.v1;

import java.util.Map;
import java.util.Objects;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
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
import com.bigdata.datashops.common.utils.JobUtils;
import com.bigdata.datashops.dao.data.domain.PageRequest;
import com.bigdata.datashops.model.DtoJobGraph;
import com.bigdata.datashops.model.dto.DtoJob;
import com.bigdata.datashops.model.dto.DtoPageQuery;
import com.bigdata.datashops.model.pojo.job.Job;
import com.bigdata.datashops.model.pojo.job.JobDependency;
import com.bigdata.datashops.model.pojo.job.JobGraph;
import com.bigdata.datashops.model.pojo.job.JobRelation;

@RestController
@RequestMapping("/v1/job")
public class JobController extends BasicController {
    @RequestMapping(value = "/getJobGraphById")
    public Result getJobGraphById(@NotNull String id) {
        JobGraph jobGraph = jobGraphService.getJobGraphByStrId(id);
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
    public Result modifyJob(@RequestBody DtoJob dtoJob) {
        Job job = new Job();
        BeanUtils.copyProperties(dtoJob, job);
        job.setStatus(1);
        jobService.save(job);
        return ok();
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

    @PostMapping(value = "/deleteJob")
    public Result deleteJob(@RequestBody Map<String, Integer> id) {
        Job job = jobService.getJob(id.get("id"));
        if (!Objects.isNull(job)) {
            job.setStatus(0);
            jobService.save(job);
        }
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
    public Result modifySchedulerStatus(@RequestBody Map<String, Object> params) {
        int status = (int) params.get("status");
        JobGraph jobGraph = jobGraphService.getJobGraph((Integer) params.get("id"));
        if (!Objects.isNull(jobGraph)) {
            jobGraph.setSchedulerStatus(status);
            jobGraphService.save(jobGraph);
        }
        return ok();
    }

    @PostMapping(value = "/addDependency")
    public Result addDependency(@RequestBody JobDependency jobDependency) {
        jobDependencyService.save(jobDependency);
        return ok();
    }

    @PostMapping(value = "/addJobToGraph")
    public Result addJobToGraph(@RequestBody Map<String, String> params) {
        String graphId = params.get("graphStrId");
        String jobId = params.get("jobStrId");
        int type = Integer.parseInt(params.get("type"));
        JobGraph jobGraph = jobGraphService.getJobGraphByStrId(graphId);
        if (Objects.isNull(jobGraph)) {
            return fail(ResultCode.FAILURE);
        }
        JobRelation jobRelation = new JobRelation();
        jobRelation.setTopPos("0px");
        jobRelation.setLeftPos("0px");
        jobRelation.setGraphStrId(graphId);
        jobRelation.setJobStrId(jobId);
        jobRelation.setNodeType(type);
        jobRelationService.save(jobRelation);
        return ok();
    }

    @PostMapping(value = "/addNewJobToGraph")
    public Result addNewJobToGraph(@RequestBody Map<String, String> params) {
        String graphId = params.get("graphId");
        int type = Integer.parseInt(params.get("type"));
        String owner = String.valueOf(params.get("name"));
        String ico = params.get("ico");
        Job job = new Job();
        // todo project id
        job.setStrId(JobUtils.genStrId("1-" + "1-"));
        job.setType(type);
        job.setOwner(owner);
        job.setIco(ico);
        job.setStatus(1);
        job = jobService.save(job);

        JobRelation jobRelation = new JobRelation();
        jobRelation.setGraphStrId(graphId);
        jobRelation.setJobStrId(job.getStrId());
        jobRelation.setNodeType(type);
        jobRelationService.save(jobRelation);
        return ok();
    }

    @PostMapping(value = "/modifyPosition")
    public Result modifyPosition(@RequestBody Map<String, String> params) {
        String graphId = params.get("graphStrId");
        String jobStrId = params.get("jobStrId");
        String[] jobIdStr = jobStrId.split(Constants.SEPARATOR_HYPHEN);
        if (jobIdStr[1].contains("+")) {
            return ok();
        }
        String filter = "graphStrId=" + graphId + ";jobStrId=" + jobStrId + ";nodeType=" + jobIdStr[1];
        JobRelation jobRelation = jobRelationService.findOneByQuery(filter);
        jobRelation.setTopPos(params.get("top"));
        jobRelation.setLeftPos(params.get("left"));
        jobRelationService.save(jobRelation);
        return ok();
    }

    @PostMapping(value = "/modifyOffset")
    public Result modifyOffset(@RequestBody Map<String, String> params) {
        String graphStrId = params.get("graphStrId");
        String sourceStrId = params.get("sourceStrId");
        String targetStrId = params.get("targetStrId");
        int offset = Integer.parseInt(params.get("offset"));
        String filter =
                String.format("graphStrId=%s;sourceStrId=%s;targetStrId=%s", graphStrId, sourceStrId, targetStrId);
        JobDependency jobDependency = jobDependencyService.getOne(filter);
        jobDependency.setOffset(offset);
        jobDependencyService.save(jobDependency);
        return ok();
    }
}
