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
import com.bigdata.datashops.service.utils.CronHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@RestController
@RequestMapping("/v1/job/dependency")
public class JobDependencyController extends BasicController {
    @PostMapping(value = "/getJobDependency")
    public Result getJobDependency(@RequestBody Map<String, String> params) {
        String maskId = params.get("id");
        List<JobDependency> jobDependencies = jobDependencyService.getJobDependency("targetId=" + maskId);
        List<VoJobDependency> vos = jobDependencyService.fillJobInfo(jobDependencies);
        return ok(vos);
    }

    @PostMapping(value = "/addDependency")
    public Result addDependency(@RequestBody JobDependency jobDependency) {
        jobDependencyService.save(jobDependency);
        return ok();
    }

    @RequestMapping(value = "/preview")
    public Result preview(@NotNull Integer id) {
        Job job = jobService.getJob(id);
        Date date = CronHelper.getNextTime(job.getCronExpression());

        Map<String, Object> result = Maps.newHashMap();
        List<Edge> edges = Lists.newArrayList();
        List<Node> nodes = Lists.newArrayList();
        Node node = new Node();
        node.setLabel(job.getName());
        node.setId(id.toString());
        nodes.add(node);

        List<JobDependency> dependencyList = jobDependencyService.getJobDependency("targetId=" + id);
        for (JobDependency dependency : dependencyList) {
            int preJobId = dependency.getSourceId();
            Job sourceJob = jobService.getJob(preJobId);
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
                Node source = new Node();
                source.setLabel(sourceJob.getName());
                source.setId(sourceDate.toString());
                nodes.add(source);

                Edge edge = new Edge();
                edge.setFrom(sourceDate.toString());
                edge.setTo(id.toString());
                edge.setLabel(DateUtils.format(sourceDate, Constants.YYYY_MM_DD_HH_MM_SS));
                edges.add(edge);
            }
        }
        result.put("nodes", nodes);
        result.put("edges", edges);
        return ok(result);
    }
}
