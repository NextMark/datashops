package com.bigdata.datashops.dao.datasource;

import com.bigdata.datashops.model.enums.DbType;

public class MySQLDataSource extends BaseDataSource {
    @Override
    public DbType dbType() {
        return DbType.MYSQL;
    }
}
