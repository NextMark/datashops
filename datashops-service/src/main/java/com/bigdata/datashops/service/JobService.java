package com.bigdata.datashops.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.bigdata.datashops.dao.data.domain.PageRequest;
import com.bigdata.datashops.dao.data.service.AbstractMysqlPagingAndSortingQueryService;
import com.bigdata.datashops.model.pojo.job.Job;

@Service
public class JobService extends AbstractMysqlPagingAndSortingQueryService<Job, Integer> {
    public Job getJob(Integer id) {
        return findById(id);
    }

    public Job getJobByStrId(String id) {
        return findOneByQuery("strId=" + id);
    }

    public List<Job> findJobs(String filters) {
        return findByQuery(filters);
    }

    public Page<Job> getJobList(PageRequest pageRequest) {
        return pageByQuery(pageRequest);
    }

}
