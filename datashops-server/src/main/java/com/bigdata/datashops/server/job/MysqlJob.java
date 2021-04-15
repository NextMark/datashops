package com.bigdata.datashops.server.job;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.bigdata.datashops.common.utils.LocalDateUtils;
import com.bigdata.datashops.dao.datasource.DataSourceFactory;
import com.bigdata.datashops.model.enums.DbType;
import com.bigdata.datashops.server.master.parser.SQLParser;

public class MysqlJob extends AbstractJob {

    public MysqlJob(JobContext jobContext) {
        super(jobContext);
    }

    @Override
    protected void process() throws Exception {
        String data = jobInstance.getJob().getData();
        baseDataSource = DataSourceFactory.getDatasource(DbType.MYSQL, data);
        DataSourceFactory.loadClass(baseDataSource.dbType());

        try {
            Connection connection = creatConnection();
            PreparedStatement ps = connection.prepareStatement(SQLParser.parseSQL(
                    LocalDateUtils.dateToLocalDateTime(jobInstance.getBizTime()), baseDataSource.getValue()));
            LOG.info("Execute mysql: {}", baseDataSource.getValue());
            ResultSet rs = ps.executeQuery();
            resultProcess(rs);
            success();
        } catch (SQLException e) {
            LOG.error(String.format("Job execute error class=%s, name=%s, instanceId=%s", this.getClass(),
                    jobInstance.getJob().getName(), jobInstance.getInstanceId()), e);
            fail();
        }
    }

    @Override
    public void after() {
    }
}
