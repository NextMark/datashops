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
    }

    @Override
    protected void process() throws Exception {
        String data = jobInstance.getData();
        SqoopData sqoopData = JSONUtils.parseObject(data, SqoopData.class);

        String[] args =
                new String[] {"--connect", sqoopData.getMysqlJdbc(), "-username", sqoopData.getMysqlUser(), "-password",
                        sqoopData.getMysqlPass(), "--table", sqoopData.getMysqlTable(), "--export-dir",
                        sqoopData.getExportDir(), "-m", sqoopData.getMapNum(), "--input-lines-terminated-by", "'\\n'",
                        "--input-fields-terminated-by", "'\\0001'"};
        String[] expandArgs = OptionsFileUtil.expandArguments(args);
        SqoopTool tool = SqoopTool.getTool("export");
        Configuration conf = new Configuration();
        conf.set("fs.default.name", PropertyUtils.getString(Constants.HDFS_DEFAULT_NAME));
        Configuration loadPlugins = SqoopTool.loadPlugins(conf);
        Sqoop sqoop = new Sqoop(tool, loadPlugins);
        Sqoop.runSqoop(sqoop, expandArgs);

        buildGrpcRequest(Constants.RPC_JOB_SUCCESS);

    }

    private void mysqlToHive(String path, String username, String password, String table) {
        try {
            String[] args =
                    new String[] {"--connect", path, "-username", username, "-password", password, "--table", table,
                            "-m", "1", "--target-dir", "java_import_user"};

            String[] expandArguments = OptionsFileUtil.expandArguments(args);

            SqoopTool tool = SqoopTool.getTool("import");

            Configuration conf = new Configuration();
            //			conf.set("fs.default.name", "hdfs://192.168.92.215:8020");// 设置HDFS服务地址
            conf.set("fs.default.name", "hdfs://192.168.92.215:8020");// 设置HDFS服务地址
            Configuration loadPlugins = SqoopTool.loadPlugins(conf);

            Sqoop sqoop = new Sqoop(tool, loadPlugins);
            System.out.println(Sqoop.runSqoop(sqoop, expandArguments));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
