package com.bigdata.datashops.dao.datasource;

import com.bigdata.datashops.model.enums.DbType;

public class HiveDataSource extends BaseDataSource {
    @Override
    public DbType dbType() {
        return DbType.HIVE;
    }
}
