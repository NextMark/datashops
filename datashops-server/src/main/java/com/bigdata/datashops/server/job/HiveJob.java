package com.bigdata.datashops.server.job;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.bigdata.datashops.dao.datasource.DataSourceFactory;
import com.bigdata.datashops.model.enums.DbType;

public class HiveJob extends AbstractJob {

    public HiveJob(JobContext jobContext) {
        super(jobContext);
    }

    @Override
    public void before() {
        LOG.info("Job before");
    }

    @Override
    public void process() throws Exception {
        try {
            String data = jobInstance.getData();
            baseDataSource = DataSourceFactory.getDatasource(DbType.HIVE, data);
            DataSourceFactory.loadClass(baseDataSource.dbType());
            Connection connection = creatConnection();
            PreparedStatement ps = connection.prepareStatement(baseDataSource.getValue());
            LOG.info("Execute hive: {}", baseDataSource.getValue());
            ResultSet rs = ps.executeQuery();
            resultProcess(rs);
            success();
        } catch (SQLException e) {
            fail();
            e.printStackTrace();
        }
    }

    @Override
    public void after() {
        LOG.info("Job end");
    }
}
