package com.bigdata.datashops.service;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.bigdata.datashops.dao.data.domain.PageRequest;
import com.bigdata.datashops.dao.data.service.AbstractMysqlPagingAndSortingQueryService;
import com.bigdata.datashops.model.pojo.user.Role;

@Service
public class RoleService extends AbstractMysqlPagingAndSortingQueryService<Role, Integer> {
    public Page<Role> getRoles(PageRequest pageRequest) {
        return pageByQuery(pageRequest);
    }

    public Role getRole(Integer id) {
        return findById(id);
    }
}
