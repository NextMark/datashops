package com.bigdata.datashops.dao.user;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.bigdata.datashops.model.pojo.user.RolePermission;

public interface RolePermissionDao extends PagingAndSortingRepository<RolePermission, Integer> {
}
