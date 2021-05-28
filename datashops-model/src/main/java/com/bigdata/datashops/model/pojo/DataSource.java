package com.bigdata.datashops.model.pojo;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "t_datasource")
public class DataSource extends BaseModel {
    private int type;

    private String name;

    private String host;

    private int port;

    private String user;

    private String password;
}
