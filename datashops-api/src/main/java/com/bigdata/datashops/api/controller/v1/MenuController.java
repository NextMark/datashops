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
import com.bigdata.datashops.model.pojo.user.RolePermission;

@RestController
@RequestMapping("/v1/menu")
public class MenuController extends BasicController {
    @PostMapping(value = "/asyncMenu")
    public Object asyncMenu() {
        int uid = getUid();
        List<Permission> permissions = permissionService.getPermissionList(uid);

        List<Integer> roleIds = permissions.stream().map(Permission::getRoleId).collect(Collectors.toList());
        String filter = "roleId=" + StringUtils.join(roleIds, Constants.SEPARATOR_COMMA);
        List<RolePermission> rolePermissions = rolePermissionService.getRolePermission(filter);

        List<Integer> menusIds = rolePermissions.stream().map(RolePermission::getMenuId).collect(Collectors.toList());
        menusIds = menusIds.parallelStream().distinct().collect(Collectors.toList());
        filter = "parentId=0;" + "id=" + StringUtils.join(menusIds, Constants.SEPARATOR_COMMA);
        List<Menu> menus = menuService.getMenus(filter);
        menuService.fillChildren(menus, menusIds);

        return ok(menus);
    }

    @PostMapping(value = "/getMenuList")
    public Object getMenuList(@RequestBody DtoPageQuery query) {
        PageRequest pageable =
                new PageRequest(query.getPageNum() - 1, query.getPageSize(), "parentId=0", Sort.Direction.ASC, "sort");
        Page<Menu> menus = menuService.getByPage(pageable);
        List<Menu> menuList = menus.getContent();
        menuService.fillChildren(menuList);
        Pagination pagination = new Pagination(menus);
        return ok(pagination);
    }

    @PostMapping(value = "/getMenuTree")
    public Object getMenuList() {
        String filter = "parentId=0";
        List<Menu> menuList = menuService.getMenus(filter);
        menuService.fillChildren(menuList);
        return ok(menuList);
    }

    @PostMapping(value = "/getRoleMenu")
    public Object getRoleMenu(@RequestBody Map<String, Object> id) {
        String filter = "roleId=" + id.get("id");
        List<RolePermission> rolePermissions = rolePermissionService.getRolePermission(filter);
        List<Integer> ids = rolePermissions.stream().map(RolePermission::getMenuId).collect(Collectors.toList());
        filter = "id=" + StringUtils.join(ids, Constants.SEPARATOR_COMMA);
        List<Menu> menus = menuService.getMenus(filter);
        return ok(menus);
    }

    @PostMapping(value = "/addBaseMenu")
    public Object addBaseMenu(@RequestBody Map<String, Object> payload) {
        Menu menu = JSONUtils.convertValue(payload, Menu.class);
        menuService.save(menu);
        return ok();
    }

    @PostMapping(value = "/getBaseMenuById")
    public Object getBaseMenuById(@RequestBody Map<String, Integer> payload) {
        Integer id = payload.get("id");
        Menu menu = menuService.findById(id);
        return ok(menu);
    }

    @PostMapping(value = "/modifyMenu")
    public Object modifyMenu(@RequestBody Menu menu) {
        menuService.save(menu);
        return ok(menu);
    }
}
