package com.bigdata.datashops.dao.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bigdata.datashops.common.Constants;
import com.bigdata.datashops.common.utils.JSONUtils;
import com.bigdata.datashops.model.enums.DbType;

public class DataSourceFactory {
    private static final Logger LOG = LoggerFactory.getLogger(DataSourceFactory.class);

    public static BaseDataSource getDatasource(DbType dbType, String parameter) {
        try {
            switch (dbType) {
                case MYSQL:
                    return JSONUtils.parseObject(parameter, MySQLDataSource.class);
                case HIVE:
                    return JSONUtils.parseObject(parameter, HiveDataSource.class);
                default:
                    return null;
            }
        } catch (Exception e) {
            LOG.error("get datasource object error", e);
            return null;
        }
    }
    public static void loadClass(DbType dbType) throws Exception{
        switch (dbType){
            case MYSQL :
                Class.forName(Constants.COM_MYSQL_JDBC_DRIVER);
                break;
            case HIVE :
                Class.forName(Constants.ORG_APACHE_HIVE_JDBC_HIVE_DRIVER);
                break;
            default:
                LOG.error("not support sql type: {}, can't load class", dbType);
                throw new IllegalArgumentException("not support sql type, can't load class");
        }
    }


}
