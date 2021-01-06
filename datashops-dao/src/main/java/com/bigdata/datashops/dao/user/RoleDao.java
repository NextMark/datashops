package com.bigdata.datashops.dao.user;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.bigdata.datashops.model.pojo.user.Role;

public interface RoleDao extends PagingAndSortingRepository<Role, Integer> {
}
