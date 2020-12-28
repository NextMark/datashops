package com.bigdata.datashops.dao.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bigdata.datashops.model.enums.DbType;

import lombok.Data;

@Data
public abstract class BaseDataSource {
    private static final Logger LOG = LoggerFactory.getLogger(BaseDataSource.class);

    protected String user;

    protected String password;

    private String address;

    private String database;

    private String params;

    protected String fillParams(String params){
        return params;
    }

    public String getJdbcUrl() {
        return "";
    }

    public abstract DbType dbType();

}
