package com.bigdata.datashops.api.controller.v1;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bigdata.datashops.api.controller.BasicController;
import com.bigdata.datashops.api.response.Result;
import com.bigdata.datashops.api.utils.ValidatorUtil;
import com.bigdata.datashops.common.utils.JSONUtils;
import com.bigdata.datashops.model.pojo.job.Job;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;

@RestController
@RequestMapping("/datashops/job")
public class JobController extends BasicController {
    @RequestMapping(value = "/startJobs")
    public Result startJobs(Long id) {
        Job job = new Job();
        ValidatorUtil.validate(job);
        return ok();
    }

    @RequestMapping(value = "/test")
    public Result test(Long id) {
        Job job = new Job();
        job.setName("test");
        ValidatorUtil.validate(job);
        //jobService.addJob(job);
        ObjectNode node = JSONUtils.createObjectNode();
        node.put("ss", "ddf");
        List<Object> r = Lists.newArrayList();
        r.add(node);
        return ok();
    }
}
