package com.bigdata.datashops.dao.user;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.bigdata.datashops.model.pojo.user.Permission;

public interface PermissionDao extends PagingAndSortingRepository<Permission, Integer> {
}
