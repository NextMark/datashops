package com.bigdata.datashops.server.job;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.bigdata.datashops.common.Constants;
import com.bigdata.datashops.common.utils.JSONUtils;
import com.bigdata.datashops.dao.datasource.DataSourceFactory;
import com.bigdata.datashops.dao.datasource.HiveDataSource;

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
        String data = instance.getData();
        baseDataSource = JSONUtils.parseObject(data, HiveDataSource.class);
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
        executeSQL();
    }

    private void executeSQL() {

    }

    @Override
    public void after() {
        LOG.info("Job end");
        grpcRemotingClient.send(request, selectHost());
    }
}
