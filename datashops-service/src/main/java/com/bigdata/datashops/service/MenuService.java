package com.bigdata.datashops.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bigdata.datashops.dao.mapper.MenuMapper;
import com.bigdata.datashops.model.pojo.user.Menu;
import com.google.common.collect.Lists;

@Service
public class MenuService {
    @Autowired
    private MenuMapper menuMapper;

    public List<Menu> getFirstMenus() {
        LambdaQueryWrapper<Menu> lqw = Wrappers.lambdaQuery();
        lqw.eq(Menu::getParentId, 0);
        lqw.orderByAsc(Menu::getSort);
        return menuMapper.selectList(lqw);
    }

    public List<Menu> getMenus(List<Integer> ids) {
        LambdaQueryWrapper<Menu> lqw = Wrappers.lambdaQuery();
        lqw.eq(Menu::getParentId, 0);
        lqw.in(Menu::getId, ids);
        lqw.orderByAsc(Menu::getSort);
        return menuMapper.selectList(lqw);
    }

    public void save(Menu entity) {
        menuMapper.insert(entity);
    }

    public Menu findById(int id) {
        return menuMapper.selectById(id);
    }

    public IPage<Menu> findList(int pageNum, int pageSize) {
        Page<Menu> page = new Page(pageNum, pageSize);
        LambdaQueryWrapper<Menu> lqw = Wrappers.lambdaQuery();
        lqw.eq(Menu::getParentId, 0);
        lqw.orderByAsc(Menu::getSort);
        return menuMapper.selectPage(page, lqw);
    }

    public void fillChildren(List<Menu> menus) {
        menus.forEach(menu -> {
            LambdaQueryWrapper<Menu> lqw = Wrappers.lambdaQuery();
            lqw.eq(Menu::getParentId, menu.getId());
            lqw.orderByAsc(Menu::getSort);
            List<Menu> children = menuMapper.selectList(lqw);
            menu.setChildren(children);
        });
    }

    public void fillChildren(List<Menu> menus, List<Integer> includeIds) {
        menus.forEach(menu -> {
            LambdaQueryWrapper<Menu> lqw = Wrappers.lambdaQuery();
            lqw.eq(Menu::getParentId, menu.getId());
            lqw.orderByAsc(Menu::getSort);
            List<Menu> children = menuMapper.selectList(lqw);
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
