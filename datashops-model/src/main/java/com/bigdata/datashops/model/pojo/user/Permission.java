package com.bigdata.datashops.model.pojo.user;

import com.baomidou.mybatisplus.annotation.TableName;
import com.bigdata.datashops.model.pojo.BaseModel;

import lombok.Data;

@Data
@TableName(value = "t_user_permission")
public class Permission extends BaseModel {
    private Integer uid;

    private Integer roleId;
}
