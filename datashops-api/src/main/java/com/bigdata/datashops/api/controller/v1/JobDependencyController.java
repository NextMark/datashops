package com.bigdata.datashops.api.controller.v1;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bigdata.datashops.api.controller.BasicController;
import com.bigdata.datashops.api.response.Result;
import com.bigdata.datashops.common.Constants;
import com.bigdata.datashops.common.utils.DateUtils;
import com.bigdata.datashops.common.utils.JSONUtils;
import com.bigdata.datashops.model.pojo.job.Edge;
import com.bigdata.datashops.model.pojo.job.Job;
import com.bigdata.datashops.model.pojo.job.JobDependency;
import com.bigdata.datashops.model.vo.VoJobDependency;
import com.bigdata.datashops.service.graph.Vertex;
import com.bigdata.datashops.service.utils.CronHelper;
import com.bigdata.datashops.service.utils.JobHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@RestController
@RequestMapping("/v1/job/dependency")
public class JobDependencyController extends BasicController {
    @PostMapping(value = "/getJobDependency")
    public Result getJobDependency(@RequestBody Map<String, String> params) {
        String maskId = params.get("id");
        List<JobDependency> jobDependencies = jobDependencyService.findByTargetId(maskId);
        List<VoJobDependency> vos = jobDependencyService.fillJobInfo(jobDependencies);
        return ok(vos);
    }

    @PostMapping(value = "/addDependency")
    public Result addDependency(@RequestBody JobDependency jobDependency) {
        jobDependencyService.save(jobDependency);
        return ok();
    }

    @PostMapping(value = "/deleteJobDependency")
    public Result deleteJobDependency(@RequestBody Map<String, Integer> params) {
        Integer id = params.get("id");
        jobDependencyService.deleteById(id);
        return ok();
    }

    @RequestMapping(value = "/preview")
    public Result preview(@NotNull String maskId) {
        Job job = jobService.getOnlineJobByMaskId(maskId);
        Date date = CronHelper.getNextExecutionTime(job.getCronExpression());

        Map<String, Object> result = Maps.newHashMap();
        List<Edge> edges = Lists.newArrayList();
        List<Vertex> nodes = Lists.newArrayList();
        Map<String, Object> extra = Maps.newHashMap();
        extra.put("bizTime", DateUtils.format(date, Constants.YYYY_MM_DD_HH_MM_SS));
        extra.put("period", job.getSchedulingPeriod());
        Vertex vertex = new Vertex(job.getMaskId(), job.getName(), job.getType(), JSONUtils.toJsonString(extra));
        nodes.add(vertex);
        List<JobDependency> dependencyList = jobDependencyService.findByTargetId(maskId);
        for (JobDependency dependency : dependencyList) {
            String preJobId = dependency.getSourceId();
            Job sourceJob = jobService.getOnlineJobByMaskId(preJobId);
            int type = dependency.getType();
            List<Integer> offsets = JobHelper.parseOffsetToList(dependency.getOffset(), type);
            for (Integer o : offsets) {
                Date sourceDate = CronHelper.getUpstreamBizTime(sourceJob.getCronExpression(), date, o);
                extra = Maps.newHashMap();
                extra.put("bizTime", DateUtils.format(sourceDate, Constants.YYYY_MM_DD_HH_MM_SS));
                extra.put("period", sourceJob.getSchedulingPeriod());
                Vertex source = new Vertex(sourceJob.getMaskId() + "_" + o, sourceJob.getName(), sourceJob.getType(),
                        JSONUtils.toJsonString(extra));
                nodes.add(source);

                Edge edge = new Edge();
                edge.setFrom(sourceJob.getMaskId() + "_" + o);
                edge.setTo(maskId);
                edge.setLabel(o.toString());
                edges.add(edge);
            }
        }

        dependencyList = jobDependencyService.findBySourceId(maskId);
        for (JobDependency dependency : dependencyList) {
            String preJobId = dependency.getTargetId();
            Job sourceJob = jobService.getOnlineJobByMaskId(maskId);
            Job targetJob = jobService.getOnlineJobByMaskId(preJobId);
            int type = dependency.getType();
            List<Integer> offsets = JobHelper.parseOffsetToList(dependency.getOffset(), type);
            for (Integer o : offsets) {
                List<Date> dates = CronHelper.getDownstreamBizTime(date, o, sourceJob.getSchedulingPeriod(),
                        sourceJob.getCronExpression(), targetJob.getCronExpression());
                for (Date bizDate : dates) {
                    extra = Maps.newHashMap();
                    extra.put("bizTime", DateUtils.format(bizDate, Constants.YYYY_MM_DD_HH_MM_SS));
                    extra.put("period", targetJob.getSchedulingPeriod());
                    Vertex source =
                            new Vertex(targetJob.getMaskId() + "_" + o, targetJob.getName(), targetJob.getType(),
                                    JSONUtils.toJsonString(extra));
                    nodes.add(source);

                    Edge edge = new Edge();
                    edge.setFrom(maskId);
                    edge.setTo(targetJob.getMaskId() + "_" + o);
                    edge.setLabel(o.toString());
                    edges.add(edge);
                }
            }
        }

        result.put("nodes", nodes);
        result.put("edges", edges);
        return ok(result);
    }
}
