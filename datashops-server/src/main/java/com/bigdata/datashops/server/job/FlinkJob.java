package com.bigdata.datashops.server.job;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.api.internal.TableEnvironmentImpl;

import com.bigdata.datashops.server.job.excutor.FlinkCommandExecutor;

public class FlinkJob extends AbstractJob {

    private FlinkCommandExecutor flinkCommandExecutor;

    public FlinkJob(JobContext jobContext) {
        super(jobContext);
        flinkCommandExecutor = new FlinkCommandExecutor(jobContext);
    }

    @Override
    protected void process() throws Exception {

        StreamExecutionEnvironment.setDefaultLocalParallelism(1);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        EnvironmentSettings settings = EnvironmentSettings.newInstance().useBlinkPlanner().inStreamingMode().build();
        TableEnvironmentImpl tEnv = (TableEnvironmentImpl) StreamTableEnvironment.create(env, settings);

        tEnv.sqlUpdate("sql");

        tEnv.execute("name");
    }

}
