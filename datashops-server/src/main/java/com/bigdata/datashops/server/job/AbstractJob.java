package com.bigdata.datashops.server.job;

import static com.bigdata.datashops.model.enums.DbType.HIVE;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;

import com.bigdata.datashops.common.Constants;
import com.bigdata.datashops.common.utils.JSONUtils;
import com.bigdata.datashops.common.utils.NetUtils;
import com.bigdata.datashops.dao.datasource.BaseDataSource;
import com.bigdata.datashops.model.pojo.job.JobInstance;
import com.bigdata.datashops.model.pojo.rpc.Host;
import com.bigdata.datashops.protocol.GrpcRequest;
import com.bigdata.datashops.server.master.dispatch.selector.RandomHostSelector;
import com.bigdata.datashops.server.rpc.GrpcRemotingClient;
import com.bigdata.datashops.server.utils.ZKUtils;
import com.bigdata.datashops.server.zookeeper.ZookeeperOperator;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.google.protobuf.ByteString;

public abstract class AbstractJob {
    protected Logger LOG;

    protected JobInstance instance;

    protected BaseDataSource baseDataSource;

    protected GrpcRemotingClient grpcRemotingClient;

    protected ZookeeperOperator zookeeperOperator;

    protected GrpcRequest.Request request;

    protected JobResult result = new JobResult();

    public AbstractJob() {
    }

    public AbstractJob(JobContext jobContext) {
        this.instance = jobContext.getJobInstance();
        this.LOG = jobContext.getLogger();
        this.grpcRemotingClient = jobContext.getGrpcRemotingClient();
        this.zookeeperOperator = jobContext.getZookeeperOperator();
        result.setInstanceId(instance.getInstanceId());
    }

    public void execute() throws Exception {
        before();
        process();
        after();
    }

    protected void before() {
    }

    protected abstract void process() throws Exception;

    protected void after() {
    }

    protected Connection creatConnection() throws SQLException {
        Connection connection;
        if (HIVE == baseDataSource.dbType()) {
            Properties paramProp = new Properties();
            paramProp.setProperty("user", baseDataSource.getUser());
            paramProp.setProperty("password", baseDataSource.getPassword());

            connection = DriverManager.getConnection(baseDataSource.getJdbcUrl(), paramProp);
        } else {
            connection = DriverManager.getConnection(baseDataSource.getJdbcUrl(), baseDataSource.getUser(),
                    baseDataSource.getPassword());
        }
        return connection;
    }

    protected Host selectHost() {
        boolean masterExist = zookeeperOperator.isExisted(ZKUtils.getMasterRegistryPath());
        if (!masterExist) {
            LOG.warn("ZK folder not exist {}", ZKUtils.getMasterRegistryPath());
            throw new RuntimeException("Master not exist");
        }
        List<String> hostsStr = zookeeperOperator.getChildrenKeys(ZKUtils.getMasterRegistryPath());
        if (hostsStr.size() == 0) {
            LOG.warn("No active master in {}", ZKUtils.getMasterRegistryPath());
            throw new RuntimeException("Master not exist");
        }
        List<Host> hosts = Lists.newArrayList();
        for (String host : hostsStr) {
            String[] hostInfo = host.split(Constants.SEPARATOR_UNDERLINE);
            Host h = Host.builder().ip(hostInfo[0]).port(Integer.parseInt(hostInfo[1])).build();
            hosts.add(h);
        }
        RandomHostSelector randomHostSelector = new RandomHostSelector(hosts);
        return randomHostSelector.select();
    }

    protected void buildGrpcRequest(CommandResult commandResult) {
        if (commandResult.getExistCode() == Constants.EXIT_SUCCESS_CODE) {
            request = GrpcRequest.Request.newBuilder().setHost(NetUtils.getLocalAddress())
                              .setRequestId(RandomUtils.nextInt())
                              .setRequestType(GrpcRequest.RequestType.JOB_EXECUTE_RESPONSE)
                              .setCode(Constants.RPC_JOB_SUCCESS)
                              .setBody(ByteString.copyFrom(JSONUtils.toJsonString(result).getBytes())).build();
        } else {
            request = GrpcRequest.Request.newBuilder().setHost(NetUtils.getLocalAddress())
                              .setRequestId(RandomUtils.nextInt())
                              .setRequestType(GrpcRequest.RequestType.JOB_EXECUTE_RESPONSE)
                              .setCode(Constants.RPC_JOB_FAIL)
                              .setBody(ByteString.copyFrom(JSONUtils.toJsonString(result).getBytes())).build();
        }
    }

    protected void buildGrpcRequest(int code) {
        request =
                GrpcRequest.Request.newBuilder().setHost(NetUtils.getLocalAddress()).setRequestId(RandomUtils.nextInt())
                        .setRequestType(GrpcRequest.RequestType.JOB_EXECUTE_RESPONSE).setCode(code)
                        .setBody(ByteString.copyFrom(JSONUtils.toJsonString(result).getBytes())).build();
    }

    public void resultProcess(ResultSet resultSet) throws SQLException {
        ArrayNode resultJSONArray = JSONUtils.createArrayNode();
        ResultSetMetaData md = resultSet.getMetaData();
        int num = md.getColumnCount();

        int rowCount = 0;

        while (rowCount < 1000 && resultSet.next()) {
            ObjectNode mapOfColValues = JSONUtils.createObjectNode();
            for (int i = 1; i <= num; i++) {
                mapOfColValues.set(md.getColumnLabel(i), JSONUtils.toJsonNode(resultSet.getObject(i)));
            }
            resultJSONArray.add(mapOfColValues);
            rowCount++;
        }
        result.setData(JSONUtils.toJsonString(resultJSONArray));
    }
}
