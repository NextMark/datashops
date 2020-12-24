package com.bigdata.datashops.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import com.bigdata.datashops.dao.data.domain.Pageable;
import com.bigdata.datashops.dao.data.service.AbstractMysqlPagingAndSortingQueryService;
import com.bigdata.datashops.model.dto.DtoJobResult;
import com.bigdata.datashops.model.pojo.job.JobResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;

@Service
public class JobResultService extends AbstractMysqlPagingAndSortingQueryService<JobResult, String> {

    @Autowired
    private JobService jobService;

    public Page<Map<String, Object>> getJobResultPage(Pageable pageable, DtoJobResult dto) {
        List<JobResult> results = findByQuery(pageable.getFilters(), pageable.getSort());
        List<Map<String, Object>> vos = Lists.newArrayList();
        JsonNode filtering = dto.getFiltering();
        for (JobResult jobResult : results) {

        }
        sort(vos, dto.orderField, dto.orderType);
        return new PageImpl<>(safeSubList(vos, pageable), pageable, vos.size());
    }


}
