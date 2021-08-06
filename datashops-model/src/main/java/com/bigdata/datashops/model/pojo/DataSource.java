package com.bigdata.datashops.model.pojo;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "t_datasource")
public class DataSource extends BaseModel {
    private int type;

    private String name;

    private String host;

    private int port;

    private String user;

    private String password;
}
