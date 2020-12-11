package com.bigdata.datashops.api.controller.v1;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bigdata.datashops.api.common.Pagination;
import com.bigdata.datashops.api.controller.BasicController;
import com.bigdata.datashops.api.response.ResultCode;
import com.bigdata.datashops.api.utils.ValidatorUtil;
import com.bigdata.datashops.dao.data.domain.PageRequest;
import com.bigdata.datashops.dao.data.domain.Pageable;
import com.bigdata.datashops.model.dto.DtoJobResult;

@RestController
@RequestMapping("/v1/job/result")
public class JobResultController extends BasicController {
    @RequestMapping(method = RequestMethod.POST)
    public Object getPage(@RequestBody DtoJobResult dto) {
        ValidatorUtil.validate(dto);
        String msg = dto.validate();
        if (msg != null) {
            return fail(ResultCode.FAILURE);
        }
        List<String> tokens = new ArrayList<>();
        if (StringUtils.isNotBlank(dto.getStartDate())) {
            tokens.add("time>=" + dto.getStartDate());
        }
        if (StringUtils.isNotBlank(dto.getEndDate())) {
            tokens.add("time<=" + dto.getEndDate());
        }
        if (dto.getItemId() != null) {
            tokens.add("itemId=" + dto.getItemId());
        }

        Pageable pageable = new PageRequest(dto.pageNum - 1, dto.pageSize, StringUtils.join(tokens, ";"),
                Sort.by(Sort.Direction.DESC, "time"));
        Page<Map<String, Object>> page = jobResultService.getJobResultPage(pageable, dto);
        Pagination pagination = new Pagination(page);
        return ok(pagination);
    }
}
