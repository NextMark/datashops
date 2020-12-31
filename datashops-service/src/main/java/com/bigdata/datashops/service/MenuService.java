package com.bigdata.datashops.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bigdata.datashops.dao.data.service.AbstractMysqlPagingAndSortingQueryService;
import com.bigdata.datashops.model.pojo.user.Menu;

@Service
public class MenuService extends AbstractMysqlPagingAndSortingQueryService<Menu, Integer> {
    public List<Menu> getMenus(String filter) {
        return findByQuery(filter);
    }
}
