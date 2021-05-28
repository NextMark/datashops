package com.bigdata.datashops.model.pojo.user;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.bigdata.datashops.model.pojo.BaseModel;

import lombok.Data;

@Data
@Entity
@Table(name = "t_user_role")
public class Role extends BaseModel {
    private String name;
}
