package com.bigdata.datashops.api.controller.v1;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bigdata.datashops.api.common.Pagination;
import com.bigdata.datashops.api.controller.BasicController;
import com.bigdata.datashops.common.Constants;
import com.bigdata.datashops.common.utils.JSONUtils;
import com.bigdata.datashops.dao.data.domain.PageRequest;
import com.bigdata.datashops.model.dto.DtoPageQuery;
import com.bigdata.datashops.model.pojo.user.Menu;
import com.bigdata.datashops.model.pojo.user.Permission;

@RestController
@RequestMapping("/v1/menu")
public class MenuController extends BasicController {
    @PostMapping(value = "/asyncMenu")
    public Object asyncMenu() {
        int uid = getUid();
        List<Permission> permissions = permissionService.getPermissionList(uid);
        List ids = permissions.stream().map(Permission::getMenuId).collect(Collectors.toList());
        String filter = "id=" + StringUtils.join(ids, Constants.SEPARATOR_COMMA);
        List<Menu> menus = menuService.getMenus(filter);
        menuService.fillChildren(menus);
        return ok(menus);
    }

    @PostMapping(value = "/getMenuList")
    public Object getMenuList(@RequestBody DtoPageQuery query) {
        int uid = getUid();
        List<Permission> permissions = permissionService.getPermissionList(uid);
        List ids = permissions.stream().map(Permission::getMenuId).collect(Collectors.toList());
        String filter = "id=" + StringUtils.join(ids, Constants.SEPARATOR_COMMA);
        PageRequest pageable =
                new PageRequest(query.getPageNum() - 1, query.getPageSize(), filter, Sort.Direction.ASC, "sort");
        Page<Menu> menus = menuService.getByPage(pageable);
        List<Menu> menuList = menus.getContent();
        menuService.fillChildren(menuList);
        Pagination pagination = new Pagination(menus);
        return ok(pagination);
    }

    @PostMapping(value = "/addBaseMenu")
    public Object addBaseMenu(@RequestBody Map<String, Object> payload) {
        Menu menu = JSONUtils.convertValue(payload, Menu.class);
        menuService.save(menu);
        return ok();
    }
}
