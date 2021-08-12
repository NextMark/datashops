package com.bigdata.datashops.api.controller.v1;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bigdata.datashops.api.controller.BasicController;
import com.bigdata.datashops.api.response.Result;
import com.bigdata.datashops.common.Constants;
import com.bigdata.datashops.common.utils.DateUtils;
import com.bigdata.datashops.model.pojo.job.Edge;
import com.bigdata.datashops.model.pojo.job.Job;
import com.bigdata.datashops.model.pojo.job.JobDependency;
import com.bigdata.datashops.model.pojo.job.Node;
import com.bigdata.datashops.model.vo.VoJobDependency;
import com.bigdata.datashops.service.graph.Vertex;
import com.bigdata.datashops.service.utils.CronHelper;
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
    public Result preview(@NotNull String id) {
        Job job = jobService.getOnlineJobByMaskId(id);
        Date date = CronHelper.getNextTime(job.getCronExpression());

        Map<String, Object> result = Maps.newHashMap();
        List<Edge> edges = Lists.newArrayList();
        List<Vertex> nodes = Lists.newArrayList();
        Vertex vertex = new Vertex(job.getMaskId(), job.getName(), DateUtils.format(date,
                Constants.YYYY_MM_DD_HH_MM_SS), job.getType(), job.getSchedulingPeriod());

        nodes.add(vertex);

        List<JobDependency> dependencyList = jobDependencyService.findByTargetId(id);
        for (JobDependency dependency : dependencyList) {
            String preJobId = dependency.getSourceId();
            Job sourceJob = jobService.getOnlineJobByMaskId(preJobId);
            int type = dependency.getType();
            String offset = dependency.getOffset();
            String[] offsetRegion = offset.split(Constants.SEPARATOR_COMMA);
            List<Integer> offsets = Lists.newArrayList();
            if (type == 1) {
                offsets = Arrays.stream(offsetRegion).map(Integer::valueOf).collect(Collectors.toList());
            } else if (type == 2) {
                int begin = Integer.parseInt(offsetRegion[0]);
                int end = Integer.parseInt(offsetRegion[1]);

                for (int i = begin; i < end; i++) {
                    offsets.add(i);
                }
            }
            for (Integer o : offsets) {
                Date sourceDate = CronHelper.getOffsetTriggerTime(sourceJob.getCronExpression(), date, o);
                Vertex source = new Vertex(sourceJob.getMaskId() + "_" + o, sourceJob.getName(),
                        DateUtils.format(sourceDate,
                        Constants.YYYY_MM_DD_HH_MM_SS), sourceJob.getType(), sourceJob.getSchedulingPeriod());
                nodes.add(source);

                Edge edge = new Edge();
                edge.setFrom(sourceJob.getMaskId() + "_" + o);
                edge.setTo(id);
                edge.setLabel(o.toString());
                edges.add(edge);
            }
        }
        result.put("nodes", nodes);
        result.put("edges", edges);
        return ok(result);
    }
}
