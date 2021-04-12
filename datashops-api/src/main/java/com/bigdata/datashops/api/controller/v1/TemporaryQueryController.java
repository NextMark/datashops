package com.bigdata.datashops.api.controller.v1;

import java.util.Map;
import java.util.Objects;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.quartz.SchedulerException;
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
import com.bigdata.datashops.model.pojo.job.Job;
import com.bigdata.datashops.model.pojo.job.TemporaryQuery;

@RestController
@RequestMapping("/v1/temQuery")
public class TemporaryQueryController extends BasicController {
    @PostMapping(value = "/getTmpQueryList")
    public Result getTmpQueryList(@RequestBody DtoPageQuery query) {
        StringBuilder filter = new StringBuilder();
        if (StringUtils.isNoneBlank(query.getName())) {
            filter.append("name?").append(query.getName());
        }
        PageRequest pageRequest =
                new PageRequest(query.getPageNum() - 1, query.getPageSize(), filter.toString(), Sort.Direction.DESC,
                        "createTime");
        Page<TemporaryQuery> projectList = temporaryQueryService.getList(pageRequest);
        Pagination pagination = new Pagination(projectList);
        return ok(pagination);
    }

    @PostMapping(value = "/addTmpQuery")
    public Result addQueue(@RequestBody TemporaryQuery temporaryQuery) {
        temporaryQueryService.save(temporaryQuery);
        return ok();
    }

    @PostMapping(value = "/deleteTmpQuery")
    public Object deleteQueue(@RequestBody Map<String, Integer> id) {
        temporaryQueryService.deleteById(id.get("id"));
        return ok();
    }

    @RequestMapping(value = "/getTmpQueryById")
    public Result getTmpQueryById(@NotNull Integer id) {
        TemporaryQuery temporaryQuery = temporaryQueryService.findById(id);
        return ok(temporaryQuery);
    }

    @PostMapping(value = "/update")
    public Result update(@RequestBody Map<String, String> params) {
        String value = params.get("value");
        TemporaryQuery temporaryQuery = temporaryQueryService.findById(Integer.valueOf(params.get("id")));
        if (!Objects.isNull(temporaryQuery)) {
            temporaryQuery.setValue(value);
            temporaryQueryService.save(temporaryQuery);
        }
        return ok();
    }
}
