package com.bigdata.datashops.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bigdata.datashops.dao.data.service.AbstractMysqlPagingAndSortingQueryService;
import com.bigdata.datashops.model.pojo.user.RolePermission;

@Service
public class RolePermissionService extends AbstractMysqlPagingAndSortingQueryService<RolePermission, Integer> {
    public List<RolePermission> getRolePermission(String filter) {
        return findByQuery(filter);
    }
}
