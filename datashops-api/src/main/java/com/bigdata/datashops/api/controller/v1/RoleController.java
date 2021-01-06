package com.bigdata.datashops.api.controller.v1;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bigdata.datashops.api.common.Pagination;
import com.bigdata.datashops.api.controller.BasicController;
import com.bigdata.datashops.common.utils.JSONUtils;
import com.bigdata.datashops.dao.data.domain.PageRequest;
import com.bigdata.datashops.model.dto.DtoPageQuery;
import com.bigdata.datashops.model.pojo.user.Permission;
import com.bigdata.datashops.model.pojo.user.Role;
import com.bigdata.datashops.model.pojo.user.RolePermission;
import com.google.common.collect.Lists;

@RestController
@RequestMapping("/v1/role")
public class RoleController extends BasicController {
    @PostMapping(value = "/asyncRoles")
    public Object asyncRoles(@RequestBody DtoPageQuery query) {
        PageRequest pageable =
                new PageRequest(query.getPageNum() - 1, query.getPageSize(), "", Sort.Direction.ASC, "id");
        Page<Role> roles = roleService.getRoles(pageable);
        Pagination pagination = new Pagination(roles);
        return ok(pagination);
    }

    @RequestMapping(value = "/getRolePermission")
    public Object getRolePermission(Integer roleId) {
        String filter = "roleId=" + roleId;
        List<RolePermission> rp = rolePermissionService.getRolePermission(filter);
        return ok(rp);
    }

    @RequestMapping(value = "/modifyRole")
    public Object modifyRole(@RequestBody Role role) {
        roleService.save(role);
        return ok();
    }

    @RequestMapping(value = "/delete")
    public Object deleteRole(@RequestBody Map<String, Integer> id) {
        roleService.deleteById(id.get("id"));
        return ok();
    }

    @PostMapping(value = "/addRoleAuthority")
    public Object addRoleAuthority(@RequestBody Map<String, Object> payload) {
        Integer roleId = (Integer) payload.get("id");
        List<RolePermission> rolePermissions = rolePermissionService.getRolePermission("roleId=" + roleId);
        List<Integer> menusIds = rolePermissions.stream().map(RolePermission::getMenuId).collect(Collectors.toList());
        List<Map<String, Object>> menus = (List<Map<String, Object>>) payload.get("menus");

        List<Integer> newMenuIds = Lists.newArrayList();
        menus.forEach(menu -> {
            Integer id = (Integer) menu.get("id");
            newMenuIds.add(id);
            if (!menusIds.contains(id)) {
                RolePermission rolePermission = new RolePermission();
                rolePermission.setMenuId(id);
                rolePermission.setRoleId(roleId);
                rolePermissionService.save(rolePermission);
            }
        });

        for (Integer id : menusIds) {
            if (!newMenuIds.contains(id)) {
                String filter = "menuId=" + id + ";roleId=" + roleId;
                rolePermissionService.deleteByQuery(filter);
            }
        }
        return ok();
    }

    @RequestMapping(value = "/changeRole")
    public Object changeRole(@RequestBody Map<String, Object> params) {
        Integer uid = (Integer) params.get("id");
        List<Integer> roleIds = JSONUtils.toList(params.get("roleIds").toString());
        List<Permission> permissions = permissionService.getPermissionList(uid);
        List<Integer> dbIds = permissions.stream().map(Permission::getRoleId).collect(Collectors.toList());
        for (Integer id : roleIds) {
            if (!dbIds.contains(id)) {
                Permission permission = new Permission();
                permission.setUid(uid);
                permission.setRoleId(id);
                permissionService.save(permission);
            }
        }
        for (Integer id : dbIds) {
            if (!roleIds.contains(id)) {
                String filter = "uid=" + uid + ";roleId=" + id;
                permissionService.deleteByQuery(filter);
            }
        }
        return ok();
    }
}
