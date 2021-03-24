package com.bigdata.datashops.server.job;

import org.apache.hadoop.conf.Configuration;
import org.apache.sqoop.Sqoop;
import org.apache.sqoop.tool.SqoopTool;
import org.apache.sqoop.util.OptionsFileUtil;

import com.bigdata.datashops.common.Constants;
import com.bigdata.datashops.common.utils.JSONUtils;
import com.bigdata.datashops.common.utils.PropertyUtils;
import com.bigdata.datashops.model.pojo.job.data.SqoopData;

public class SqoopJob extends AbstractJob {
    public SqoopJob(JobContext jobContext) {
        super(jobContext);
        sqoopData = JSONUtils.parseObject(jobInstance.getData(), SqoopData.class);
    }

    private SqoopData sqoopData;

    @Override
    protected void process() throws Exception {
        if (sqoopData.getType() == 1) {
            hiveToMysql();
        } else {
            mysqlToHive();
        }
        buildGrpcRequest(Constants.RPC_JOB_SUCCESS);
    }

    private void hiveToMysql() {
        try {
            String[] args = new String[] {"--connect", sqoopData.getMysqlJdbc(), "-username", sqoopData.getMysqlUser(),
                    "-password", sqoopData.getMysqlPass(), "--table", sqoopData.getMysqlTable(), "--export-dir",
                    sqoopData.getExportDir(), "-m", sqoopData.getMapNum(), "--input-lines-terminated-by",
                    sqoopData.getLinesTerminated(), "--input-fields-terminated-by", sqoopData.getFieldsTerminated()};
            String[] expandArgs = OptionsFileUtil.expandArguments(args);
            SqoopTool tool = SqoopTool.getTool("export");
            Configuration conf = new Configuration();
            conf.set("fs.default.name", PropertyUtils.getString(Constants.HDFS_DEFAULT_NAME));
            Configuration loadPlugins = SqoopTool.loadPlugins(conf);
            Sqoop sqoop = new Sqoop(tool, loadPlugins);
            Sqoop.runSqoop(sqoop, expandArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mysqlToHive() {
        try {
            String[] args = new String[] {"--connect", sqoopData.getMysqlJdbc(), "-username", sqoopData.getMysqlUser(),
                    "-password", sqoopData.getMysqlPass(), "--table", sqoopData.getMysqlTable(),
                    "--hive-drop-import-delims", "--hive-import", "-hive-overwrite", "--hive-database",
                    sqoopData.getHiveDb(), "--hive-table", sqoopData.getHiveTable(), "--hive-partition-key",
                    sqoopData.getPartitionKey(), "--hive-partition-value", sqoopData.getPartitionValue(), "-m",
                    sqoopData.getMapNum(), "--fields-terminated-by", sqoopData.getFieldsTerminated()};

            String[] expandArguments = OptionsFileUtil.expandArguments(args);
            SqoopTool tool = SqoopTool.getTool("import");
            Configuration conf = new Configuration();
            conf.set("fs.default.name", PropertyUtils.getString(Constants.HDFS_DEFAULT_NAME));
            Configuration loadPlugins = SqoopTool.loadPlugins(conf);
            Sqoop sqoop = new Sqoop(tool, loadPlugins);
            Sqoop.runSqoop(sqoop, expandArguments);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
