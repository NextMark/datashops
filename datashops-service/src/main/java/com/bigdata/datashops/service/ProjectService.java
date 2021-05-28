package com.bigdata.datashops.service;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.bigdata.datashops.dao.data.domain.PageRequest;
import com.bigdata.datashops.dao.data.service.AbstractMysqlPagingAndSortingQueryService;
import com.bigdata.datashops.model.pojo.Project;

@Service
public class ProjectService extends AbstractMysqlPagingAndSortingQueryService<Project, Integer> {
    public Project findOne(String filter) {
        return findOneByQuery(filter);
    }

    public void saveEntity(Project entity) {
        save(entity);
    }

    public Page<Project> getList(PageRequest pageRequest) {
        return pageByQuery(pageRequest);
    }
}
