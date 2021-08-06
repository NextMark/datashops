package com.bigdata.datashops.model.pojo.user;

import com.baomidou.mybatisplus.annotation.TableName;
import com.bigdata.datashops.model.pojo.BaseModel;

import lombok.Data;

@Data
@TableName(value = "t_user_role_permission")
public class RolePermission extends BaseModel {
    private Integer roleId;

    private Integer menuId;
}
