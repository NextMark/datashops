package com.bigdata.datashops.api.controller.v1;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bigdata.datashops.api.controller.BasicController;
import com.bigdata.datashops.api.response.Result;
import com.bigdata.datashops.model.pojo.job.JobDependency;
import com.bigdata.datashops.model.vo.VoJobDependency;

@RestController
@RequestMapping("/v1/jobDependency")
public class JobDependencyController extends BasicController {
    @PostMapping(value = "/getJobDependency")
    public Result getJobDependency(@RequestBody Map<String, String> params) {
        String maskId = params.get("maskId");
        List<JobDependency> jobDependencies = jobDependencyService.getJobDependency("targetMaskId=" + maskId);
        List<VoJobDependency> vos = jobDependencyService.fillJobInfo(jobDependencies);
        return ok(vos);
    }

    @PostMapping(value = "/addDependency")
    public Result addDependency(@RequestBody JobDependency jobDependency) {
        jobDependencyService.save(jobDependency);
        return ok();
    }
}
