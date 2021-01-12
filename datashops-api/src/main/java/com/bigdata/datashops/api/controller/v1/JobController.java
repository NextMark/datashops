package com.bigdata.datashops.api.controller.v1;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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
import com.bigdata.datashops.dao.data.domain.PageRequest;
import com.bigdata.datashops.model.DtoJobGraph;
import com.bigdata.datashops.model.dto.DtoJob;
import com.bigdata.datashops.model.dto.DtoPageQuery;
import com.bigdata.datashops.model.pojo.job.Job;
import com.bigdata.datashops.model.pojo.job.JobDependency;
import com.bigdata.datashops.model.pojo.job.JobGraph;
import com.google.common.collect.Sets;

@RestController
@RequestMapping("/v1/job")
public class JobController extends BasicController {
    @RequestMapping(value = "/getJobGraphById")
    public Result getJobGraphById(@NotNull Integer id) {
        JobGraph jobGraph = jobGraphService.getJobGraph(id);
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
    public Result addJobToGraph(@RequestBody Map<String, Integer> params) {
        int graphId = params.get("graphId");
        int jobId = params.get("jobId");
        int type = params.get("type");
        JobGraph jobGraph = jobGraphService.getJobGraph(graphId);
        if (Objects.isNull(jobGraph)) {
            return fail(ResultCode.FAILURE);
        }
        List<Integer> ids =
                Arrays.stream(jobGraph.getJobIds().split(Constants.SEPARATOR_COMMA)).mapToInt(Integer::parseInt).boxed()
                        .collect(Collectors.toList());
        Set<Integer> jobIds = Sets.newHashSet(ids);
        if (!jobIds.contains(jobId)) {
            jobIds.add(jobId);
        } else {
            return ok();
        }
        jobGraph.setJobIds(StringUtils.join(jobIds, Constants.SEPARATOR_COMMA));
        jobGraphService.save(jobGraph);
        return ok();
    }

    @PostMapping(value = "/addNewJobToGraph")
    public Result addNewJobToGraph(@RequestBody DtoJob dtoJob) {
        ValidatorUtil.validate(dtoJob);
        Job job = new Job();
        BeanUtils.copyProperties(dtoJob, job);
        job.setStatus(1);
        jobService.save(job);
        return ok();
    }
}
