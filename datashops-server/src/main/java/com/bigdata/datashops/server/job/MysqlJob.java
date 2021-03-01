package com.bigdata.datashops.server.job;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.bigdata.datashops.common.Constants;
import com.bigdata.datashops.common.utils.JSONUtils;
import com.bigdata.datashops.dao.datasource.DataSourceFactory;
import com.bigdata.datashops.dao.datasource.MySQLDataSource;

public class MysqlJob extends AbstractJob {

    public MysqlJob(JobContext jobContext) {
        super(jobContext);
    }

    @Override
    protected void process() throws Exception {
        String data = instance.getData();

        baseDataSource = JSONUtils.parseObject(data, MySQLDataSource.class);
        DataSourceFactory.loadClass(baseDataSource.dbType());

        try {
            Connection connection = creatConnection();
            PreparedStatement ps = connection.prepareStatement(baseDataSource.getData());
            ResultSet rs = ps.executeQuery();
            resultProcess(rs);
            buildGrpcRequest(Constants.RPC_JOB_SUCCESS);
        } catch (SQLException e) {
            buildGrpcRequest(Constants.RPC_JOB_FAIL);
            e.printStackTrace();
        }
    }

    @Override
    public void after() {
        LOG.info("Job end");
        grpcRemotingClient.send(request, selectHost());
    }
}
