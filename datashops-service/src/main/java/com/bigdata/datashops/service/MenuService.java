package com.bigdata.datashops.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.bigdata.datashops.dao.data.domain.PageRequest;
import com.bigdata.datashops.dao.data.service.AbstractMysqlPagingAndSortingQueryService;
import com.bigdata.datashops.model.pojo.user.Menu;
import com.google.common.collect.Lists;

@Service
public class MenuService extends AbstractMysqlPagingAndSortingQueryService<Menu, Integer> {
    public List<Menu> getMenus(String filter) {
        return findByQuery(filter, Sort.by(Sort.Direction.ASC, "sort"));
    }

    public Page<Menu> getByPage(PageRequest pageable) {
        return pageByQuery(pageable);
    }

    public void fillChildren(List<Menu> menus) {
        menus.forEach(menu -> {
            String filter = "parentId=" + menu.getId();
            List<Menu> children = findByQuery(filter, Sort.by(Sort.Direction.ASC, "sort"));
            menu.setChildren(children);
        });
    }

    public void fillChildren(List<Menu> menus, List<Integer> includeIds) {
        menus.forEach(menu -> {
            String filter = "parentId=" + menu.getId();
            List<Menu> children = findByQuery(filter, Sort.by(Sort.Direction.ASC, "sort"));
            List<Menu> data = Lists.newArrayList();
            for (Menu m : children) {
                if (includeIds.contains(m.getId())) {
                    data.add(m);
                }
            }
            menu.setChildren(data);
        });
    }
}
