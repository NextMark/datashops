package com.bigdata.datashops.api.controller.v1;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bigdata.datashops.api.common.Pagination;
import com.bigdata.datashops.api.controller.BasicController;
import com.bigdata.datashops.common.utils.JSONUtils;
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
        IPage<Role> res = roleService.findList(query.getPageNum(), query.getPageSize());
        Pagination pagination = new Pagination(res);
        return ok(pagination);
    }

    @RequestMapping(value = "/getRolePermission")
    public Object getRolePermission(Integer roleId) {
        List<RolePermission> rp = rolePermissionService.getRolePermissionByRoleIds(Collections.singletonList(roleId));
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
        List<RolePermission> rolePermissions =
                rolePermissionService.getRolePermissionByRoleIds(Collections.singletonList(roleId));
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
                LambdaQueryWrapper<RolePermission> wrapper = Wrappers.lambdaQuery();
                wrapper.eq(RolePermission::getMenuId, id);
                wrapper.eq(RolePermission::getRoleId, roleId);
                rolePermissionService.deleteByWrapper(wrapper);
            }
        }
        return ok();
    }

    @RequestMapping(value = "/changeRole")
    public Object changeRole(@RequestBody Map<String, Object> params) {
        Integer uid = (Integer) params.get("id");
        List<Integer> roleIds = JSONUtils.toList(params.get("roleIds").toString());
        List<Permission> permissions = permissionService.findList(uid);
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
                LambdaQueryWrapper<Permission> wrapper = Wrappers.lambdaQuery();
                wrapper.eq(Permission::getUid, uid);
                wrapper.eq(Permission::getRoleId, id);
                permissionService.deleteByWrapper(wrapper);
            }
        }
        return ok();
    }
}
