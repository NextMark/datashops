package com.bigdata.datashops.server.job;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hive.jdbc.HiveStatement;

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

    private HiveStatement stmt = null;
    private Connection connection = null;
    private ResultSet rs = null;
    private String appId;

    private final String regex = "Running with YARN Application = .*?([^\\n]*)";
    private final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);

    @Override
    public void before() {
    }

    @Override
    public void process() throws Exception {
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
            resultProcess(rs);
            LOG.info("Hive job submit");
            success();
        } catch (SQLException e) {
            LOG.error(String.format("Job execute error class=%s, name=%s, instanceId=%s", this.getClass(),
                    jobInstance.getJob().getName(), jobInstance.getInstanceId()), e);
            fail();
        } finally {
            LOG.info("finally");
        }
    }

    @Override
    public void after() {
        try {
            LOG.info("close");

            if (rs != null) {
                rs.close();
            }
            LOG.info("st pre");
            if (!stmt.isClosed()) {
                LOG.info("st close");
                stmt.close();
            }
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
                            LOG.info(log.replace("INFO  : ", ""));
                            if (log.contains("Running with YARN Application")) {
                                appId = getAppId(log);
                                result.setData(appId);
                                //rpc(Constants.RPC_JOB_APP_ID);
                            }
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

    private String getAppId(String log) {
        final Matcher matcher = pattern.matcher(log);

        while (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}
