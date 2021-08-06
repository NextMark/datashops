package com.bigdata.datashops.api.controller.v1;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bigdata.datashops.api.common.Pagination;
import com.bigdata.datashops.api.controller.BasicController;
import com.bigdata.datashops.api.response.Result;
import com.bigdata.datashops.common.utils.JSONUtils;
import com.bigdata.datashops.model.dto.DtoPageQuery;
import com.bigdata.datashops.model.pojo.user.Menu;
import com.bigdata.datashops.model.pojo.user.Permission;
import com.bigdata.datashops.model.pojo.user.RolePermission;

@RestController
@RequestMapping("/v1/menu")
public class MenuController extends BasicController {
    @PostMapping(value = "/asyncMenu")
    public Result asyncMenu() {
        int uid = getUid();
        List<Permission> permissions = permissionService.findList(uid);

        List<Integer> roleIds = permissions.stream().map(Permission::getRoleId).collect(Collectors.toList());
        List<RolePermission> rolePermissions = rolePermissionService.getRolePermissionByRoleIds(roleIds);

        List<Integer> menusIds = rolePermissions.stream().map(RolePermission::getMenuId).collect(Collectors.toList());
        menusIds = menusIds.parallelStream().distinct().collect(Collectors.toList());
        List<Menu> menus = menuService.getMenus(menusIds);
        menuService.fillChildren(menus, menusIds);

        return ok(menus);
    }

    @PostMapping(value = "/getMenuList")
    public Result getMenuList(@RequestBody DtoPageQuery query) {
        IPage<Menu> res = menuService.findList(query.getPageNum(), query.getPageSize());
        List<Menu> menuList = res.getRecords();
        menuService.fillChildren(menuList);
        Pagination pagination = new Pagination(res);
        return ok(pagination);
    }

    @PostMapping(value = "/getMenuTree")
    public Result getMenuList() {
        List<Menu> menuList = menuService.getFirstMenus();
        menuService.fillChildren(menuList);
        return ok(menuList);
    }

    @PostMapping(value = "/getRoleMenu")
    public Result getRoleMenu(@RequestBody Map<String, Integer> id) {
        List<RolePermission> rolePermissions =
                rolePermissionService.getRolePermissionByRoleIds(Collections.singletonList(id.get("id")));
        List<Integer> ids = rolePermissions.stream().map(RolePermission::getMenuId).collect(Collectors.toList());
        List<Menu> menus = menuService.getMenus(ids);
        return ok(menus);
    }

    @PostMapping(value = "/addBaseMenu")
    public Result addBaseMenu(@RequestBody Map<String, Object> payload) {
        Menu menu = JSONUtils.convertValue(payload, Menu.class);
        menuService.save(menu);
        return ok();
    }

    @PostMapping(value = "/getBaseMenuById")
    public Result getBaseMenuById(@RequestBody Map<String, Integer> payload) {
        Integer id = payload.get("id");
        Menu menu = menuService.findById(id);
        return ok(menu);
    }

    @PostMapping(value = "/modifyMenu")
    public Result modifyMenu(@RequestBody Menu menu) {
        menuService.save(menu);
        return ok(menu);
    }
}
