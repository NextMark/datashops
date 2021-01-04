package com.bigdata.datashops.api.controller.v1;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bigdata.datashops.api.common.Pagination;
import com.bigdata.datashops.api.controller.BasicController;
import com.bigdata.datashops.dao.data.domain.PageRequest;
import com.bigdata.datashops.model.dto.DtoPageQuery;
import com.bigdata.datashops.model.pojo.user.Role;
import com.bigdata.datashops.model.pojo.user.RolePermission;

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
}
