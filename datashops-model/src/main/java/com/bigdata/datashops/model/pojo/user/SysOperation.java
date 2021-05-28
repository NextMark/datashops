package com.bigdata.datashops.model.pojo.user;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.bigdata.datashops.model.pojo.BaseModel;

import lombok.Data;

@Data
@Entity
@Table(name = "t_sys_operation")
public class SysOperation extends BaseModel {
    private String operator;

    private String ip;

    private Integer code;

    private String method;

    private String path;

    private Integer spend;
}
