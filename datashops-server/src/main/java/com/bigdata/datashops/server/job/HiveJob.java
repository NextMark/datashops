package com.bigdata.datashops.server.job;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.hive.jdbc.HiveStatement;

import com.bigdata.datashops.common.Constants;
import com.bigdata.datashops.common.utils.LocalDateUtils;
import com.bigdata.datashops.dao.datasource.DataSourceFactory;
import com.bigdata.datashops.model.enums.DbType;
import com.bigdata.datashops.server.master.parser.SQLParser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HiveJob extends AbstractJob {

    public HiveJob(JobContext jobContext) {
        super(jobContext);
    }

    static HiveStatement stmt = null;

    @Override
    public void before() {
    }

    @Override
    public void process() throws Exception {
        Connection connection = null;
        ResultSet rs = null;
        try {
            String data = jobInstance.getJob().getData();
            baseDataSource = DataSourceFactory.getDatasource(DbType.HIVE, data);
            DataSourceFactory.loadClass(baseDataSource.dbType());
            connection = creatConnection();
            stmt = (HiveStatement) connection.createStatement();
            LOG.info("Execute hive\n{}", baseDataSource.getValue());
            new GetLogThread().start();
            rs = stmt.executeQuery(SQLParser.parseSQL(LocalDateUtils.dateToLocalDateTime(jobInstance.getBizTime()),
                    baseDataSource.getValue()));
            String appId = "";
            for (String log : stmt.getQueryLog()) {
                if (log.contains("App id")) {
                    appId = log.substring(log.indexOf("App id") + 7, log.length() - 1);
                }
            }
            resultProcess(rs);
            LOG.info("Hive job submit, appid = {}", appId);
            result.setData(appId);
            rpc(Constants.RPC_JOB_APP_ID);
        } catch (SQLException e) {
            LOG.error(String.format("Job execute error class=%s, name=%s, instanceId=%s", this.getClass(),
                    jobInstance.getJob().getName(), jobInstance.getInstanceId()), e);
            fail();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void after() {
    }

    class GetLogThread extends Thread {
        @Override
        public void run() {
            if (stmt == null) {
                return;
            }
            try {
                while (!stmt.isClosed() && stmt.hasMoreLogs()) {
                    try {
                        for (String log : stmt.getQueryLog(true, 100)) {
                            LOG.info(log);
                        }
                        sleep(500L);
                    } catch (SQLException | InterruptedException e) {
                        e.printStackTrace();
                        return;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
