package com.bigdata.datashops.api.controller.v1;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bigdata.datashops.api.common.Pagination;
import com.bigdata.datashops.api.controller.BasicController;
import com.bigdata.datashops.api.response.Result;
import com.bigdata.datashops.dao.data.domain.PageRequest;
import com.bigdata.datashops.model.dto.DtoPageQuery;
import com.bigdata.datashops.model.pojo.job.JobInstance;

@RestController
@RequestMapping("/v1/job/instance")
public class JobInstanceController extends BasicController {
    @PostMapping(value = "/getJobInstanceList")
    public Result getJobInstanceList(@RequestBody DtoPageQuery query) {
        StringBuilder filter = new StringBuilder("status=1");
        if (StringUtils.isNoneBlank(query.getName())) {
            filter.append(";name?").append(query.getName());
        }
        if (StringUtils.isNoneBlank(query.getOwner())) {
            filter.append(";operator?").append(query.getOwner());
        }
        PageRequest pageRequest =
                new PageRequest(query.getPageNum() - 1, query.getPageSize(), filter.toString(), Sort.Direction.DESC,
                        "createTime");
        Page<JobInstance> jobInstances = jobInstanceService.getJobInstanceList(pageRequest);
        List<JobInstance> jobInstanceList = jobInstances.getContent();
        jobInstanceService.fillJob(jobInstanceList);
        Pagination pagination = new Pagination(jobInstances);
        pagination.setContent(jobInstanceList);
        return ok(pagination);
    }
}
