package com.bigdata.datashops.model.pojo.user;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.bigdata.datashops.model.pojo.BaseModel;

import lombok.Data;

@Data
@Entity
@Table(name = "t_user_permission")
public class Permission extends BaseModel {
    private int uid;

    private int menuId;
}
