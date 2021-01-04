package com.bigdata.datashops.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.bigdata.datashops.dao.data.domain.PageRequest;
import com.bigdata.datashops.dao.data.service.AbstractMysqlPagingAndSortingQueryService;
import com.bigdata.datashops.model.pojo.user.Menu;

@Service
public class MenuService extends AbstractMysqlPagingAndSortingQueryService<Menu, Integer> {
    public List<Menu> getMenus(String filter) {
        return findByQuery(filter);
    }

    public Page<Menu> getByPage(PageRequest pageable) {
        return pageByQuery(pageable);
    }

    public void fillChildren(List<Menu> menus) {
        menus.forEach(menu -> {
            String filter = "parentId=" + menu.getId();
            List<Menu> children = getMenus(filter);
            menu.setChildren(children);
        });
    }
}
