package com.bigdata.datashops.api.controller.v1;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.constraints.NotNull;

import org.glassfish.jersey.internal.guava.Sets;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedPseudograph;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bigdata.datashops.api.common.Pagination;
import com.bigdata.datashops.api.controller.BasicController;
import com.bigdata.datashops.api.response.Result;
import com.bigdata.datashops.common.utils.JSONUtils;
import com.bigdata.datashops.model.dto.DtoPageQuery;
import com.bigdata.datashops.model.pojo.job.Edge;
import com.bigdata.datashops.model.pojo.job.Job;
import com.bigdata.datashops.model.pojo.job.JobInstance;
import com.bigdata.datashops.model.pojo.job.RelationshipEdge;
import com.bigdata.datashops.service.graph.Vertex;
import com.bigdata.datashops.service.utils.GraphHelper;
import com.google.common.collect.Maps;

@RestController
@RequestMapping("/v1/job/instance")
public class JobInstanceController extends BasicController {
    @PostMapping(value = "/getJobInstanceList")
    public Result getJobInstanceList(@RequestBody DtoPageQuery query) {
        IPage<JobInstance> instanceIPage = jobInstanceService
                                                   .findByNameAndOperator(query.getPageNum(), query.getPageSize(),
                                                           query.getName(), query.getOwner());

        List<JobInstance> jobInstanceList = instanceIPage.getRecords();
        jobInstanceService.fillJob(jobInstanceList);
        Pagination pagination = new Pagination(instanceIPage);
        return ok(pagination);
    }

    @RequestMapping(value = "/getJobInstanceGraph")
    public Result getJobInstanceGraph(@NotNull String instanceId) {
        DirectedWeightedPseudograph<String, RelationshipEdge> dag =
                new DirectedWeightedPseudograph<>(RelationshipEdge.class);
        graphService.buildInstanceGraph(dag, instanceId);
        Map<String, Object> res = GraphHelper.parseToGraph(dag);
        return ok(res);
    }
}
