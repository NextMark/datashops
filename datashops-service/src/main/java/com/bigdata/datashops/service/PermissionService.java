package com.bigdata.datashops.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bigdata.datashops.dao.data.service.AbstractMysqlPagingAndSortingQueryService;
import com.bigdata.datashops.model.pojo.user.Permission;

@Service
public class PermissionService extends AbstractMysqlPagingAndSortingQueryService<Permission, Integer> {
    public List<Permission> getPermissionList(Integer uid) {
        String filter = "uid=" + uid;
        return findByQuery(filter);
    }
}
