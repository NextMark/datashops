package com.bigdata.datashops.model.pojo.user;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.bigdata.datashops.model.pojo.BaseModel;

import lombok.Data;

@Data
@Entity
@Table(name = "t_user_role_permission")
public class RolePermission extends BaseModel {
    private Integer roleId;

    private Integer menuId;
}
