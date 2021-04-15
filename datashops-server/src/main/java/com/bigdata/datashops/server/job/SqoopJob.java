package com.bigdata.datashops.server.job;

import org.apache.commons.lang3.StringUtils;
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
        sqoopData = JSONUtils.parseObject(jobInstance.getJob().getData(), SqoopData.class);
    }

    private SqoopData sqoopData;

    @Override
    protected void process() {
        try {
            if (sqoopData.getType() == 1) {
                hiveToMysql();
            } else {
                mysqlToHive();
            }
            success();
        } catch (Exception e) {
            LOG.error(String.format("Job execute error class=%s, name=%s, instanceId=%s", this.getClass(),
                    jobInstance.getJob().getName(), jobInstance.getInstanceId()), e);
            fail();
        }

    }

    private void hiveToMysql() {
        try {
            String[] args = sqoopData.buildHive2Mysql();
            LOG.info("Sqoop hive to mysql command, {}", StringUtils.join(" ", args));
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
            String[] args = sqoopData.buildMysql2Hive();
            LOG.info("Sqoop mysql to hive command\n{}", StringUtils.join(" ", args));
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
