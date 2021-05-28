package com.bigdata.datashops.dao.job;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.bigdata.datashops.model.pojo.Project;

public interface ProjectDao extends PagingAndSortingRepository<Project, Integer> {
}
