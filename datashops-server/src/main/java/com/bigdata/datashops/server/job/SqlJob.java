package com.bigdata.datashops.server.job;

import static com.bigdata.datashops.model.enums.DbType.HIVE;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.slf4j.Logger;

import com.bigdata.datashops.common.utils.JSONUtils;
import com.bigdata.datashops.dao.datasource.BaseDataSource;
import com.bigdata.datashops.dao.datasource.DataSourceFactory;
import com.bigdata.datashops.model.enums.DbType;
import com.bigdata.datashops.model.pojo.extend.SqlParams;
import com.bigdata.datashops.model.pojo.job.JobInstance;

public class SqlJob extends AbstractJob {

    private SqlParams sqlParams;

    private BaseDataSource baseDataSource;

    public SqlJob(JobContext jobContext, Logger logger) {
        super(jobContext, logger);
    }

    @Override
    public void handle() {
        JobInstance jobInstance = jobContext.getJobInstance();
        String extendData = jobInstance.getExtendData();
        sqlParams = JSONUtils.parseObject(extendData, SqlParams.class);
        baseDataSource = DataSourceFactory.getDatasource(DbType.of(sqlParams.getType()), extendData);
    }

    private Connection creatConnection() throws SQLException {
        Connection connection;
        if (HIVE == DbType.of(sqlParams.getType())) {
            Properties paramProp = new Properties();
            paramProp.setProperty("user", baseDataSource.getUser());
            paramProp.setProperty("password", baseDataSource.getPassword());

            connection = DriverManager.getConnection(baseDataSource.getJdbcUrl(), paramProp);
        } else {
            connection = DriverManager.getConnection(baseDataSource.getJdbcUrl(), baseDataSource.getUser(),
                    baseDataSource.getPassword());
        }
        return connection;
    }

    private void executeSQL() {

    }
}
