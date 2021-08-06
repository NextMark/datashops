package com.bigdata.datashops.api.controller.v1;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bigdata.datashops.api.common.Pagination;
import com.bigdata.datashops.api.controller.BasicController;
import com.bigdata.datashops.api.response.Result;
import com.bigdata.datashops.model.dto.DtoPageQuery;
import com.bigdata.datashops.model.pojo.job.JobInstance;

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
}
