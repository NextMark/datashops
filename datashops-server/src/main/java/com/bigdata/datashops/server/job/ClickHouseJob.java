package com.bigdata.datashops.server.job;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.bigdata.datashops.dao.datasource.DataSourceFactory;
import com.bigdata.datashops.model.enums.DbType;

public class ClickHouseJob extends AbstractJob {
    public ClickHouseJob(JobContext jobContext) {
        super(jobContext);
    }

    @Override
    protected void process() throws Exception {
        String data = jobInstance.getData();
        baseDataSource = DataSourceFactory.getDatasource(DbType.CLICK_HOUSE, data);
        DataSourceFactory.loadClass(baseDataSource.dbType());
        try {
            Connection connection = creatConnection();
            PreparedStatement ps = connection.prepareStatement(baseDataSource.getValue());
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
