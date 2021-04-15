package com.bigdata.datashops.server.job;

import static com.bigdata.datashops.model.enums.DbType.HIVE;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bigdata.datashops.common.Constants;
import com.bigdata.datashops.common.utils.JSONUtils;
import com.bigdata.datashops.common.utils.NetUtils;
import com.bigdata.datashops.common.utils.PropertyUtils;
import com.bigdata.datashops.dao.datasource.BaseDataSource;
import com.bigdata.datashops.model.enums.HostSelector;
import com.bigdata.datashops.model.pojo.job.JobInstance;
import com.bigdata.datashops.model.pojo.rpc.Host;
import com.bigdata.datashops.protocol.GrpcRequest;
import com.bigdata.datashops.remote.rpc.GrpcRemotingClient;
import com.bigdata.datashops.server.master.selector.AssignSelector;
import com.bigdata.datashops.server.master.selector.RandomHostSelector;
import com.bigdata.datashops.server.master.selector.ScoreSelector;
import com.bigdata.datashops.server.utils.ZKUtils;
import com.bigdata.datashops.service.zookeeper.ZookeeperOperator;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.google.protobuf.ByteString;

public abstract class AbstractJob {
    protected Logger logger = LoggerFactory.getLogger(AbstractJob.class);
    protected Logger LOG;

    protected JobInstance jobInstance;

    protected BaseDataSource baseDataSource;

    protected GrpcRemotingClient grpcRemotingClient;

    protected ZookeeperOperator zookeeperOperator;

    protected GrpcRequest.Request request;

    protected CommandResult commandResult;

    protected JobResult result = new JobResult();

    public AbstractJob() {
    }

    public AbstractJob(JobContext jobContext) {
        this.jobInstance = jobContext.getJobInstance();
        this.LOG = jobContext.getLogger();
        this.grpcRemotingClient = jobContext.getGrpcRemotingClient();
        this.zookeeperOperator = jobContext.getZookeeperOperator();
        result.setInstanceId(jobInstance.getInstanceId());
    }

    public void execute() throws Exception {
        LOG.info("Job {} begin, name={}, instanceId={}", this.getClass(), jobInstance.getJob().getName(),
                jobInstance.getInstanceId());
        before();
        process();
        after();
        LOG.info("Job {} end, name={}, instanceId={}", this.getClass(), jobInstance.getJob().getName(),
                jobInstance.getInstanceId());
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
            Host h = new Host();
            h.setIp(hostInfo[0]);
            h.setPort(Integer.parseInt(hostInfo[1]));
            hosts.add(h);
        }
        Host host = new Host();
        HostSelector selector = HostSelector.of(jobInstance.getJob().getHostSelector());
        switch (selector) {
            case ASSIGN:
                host.setIp(jobInstance.getJob().getHost());
                host.setPort(PropertyUtils.getInt(Constants.WORKER_GRPC_SERVER_PORT));
                host = new AssignSelector().select(Collections.singleton(host));
                break;
            case SCORE:
                host = new ScoreSelector().select(hosts);
                break;
            default:
                host = new RandomHostSelector().select(hosts);
        }
        LOG.info("Select {}", host.toString());
        return host;
    }

    protected void commandRpc() {
        if (!Objects.isNull(commandResult)) {
            if (commandResult.getExistCode().equals(Constants.RPC_JOB_SUCCESS)) {
                success();
            } else {
                fail();
            }
        } else {
            fail();
        }
    }

    protected void success() {
        try {
            request = GrpcRequest.Request.newBuilder().setIp(NetUtils.getLocalAddress())
                              .setPort(PropertyUtils.getInt(Constants.WORKER_GRPC_SERVER_PORT))
                              .setRequestId(jobInstance.getInstanceId())
                              .setRequestType(GrpcRequest.RequestType.JOB_EXECUTE_RESPONSE)
                              .setCode(Constants.RPC_JOB_SUCCESS)
                              .setBody(ByteString.copyFrom(JSONUtils.toJsonString(result).getBytes())).build();
            grpcRemotingClient.send(request, selectHost());
        } catch (Exception e) {
            LOG.error(String.format("Report end status error, name=%s, instanceId=%s", jobInstance.getJob().getName(),
                    jobInstance.getInstanceId()), e);
        }
    }

    protected void fail() {
        try {
            request = GrpcRequest.Request.newBuilder().setIp(NetUtils.getLocalAddress())
                              .setPort(PropertyUtils.getInt(Constants.WORKER_GRPC_SERVER_PORT))
                              .setRequestId(jobInstance.getInstanceId())
                              .setRequestType(GrpcRequest.RequestType.JOB_EXECUTE_RESPONSE)
                              .setCode(Constants.RPC_JOB_FAIL)
                              .setBody(ByteString.copyFrom(JSONUtils.toJsonString(result).getBytes())).build();
            grpcRemotingClient.send(request, selectHost());
        } catch (Exception e) {
            LOG.error(String.format("Report end status error, name=%s, instanceId=%s", jobInstance.getJob().getName(),
                    jobInstance.getInstanceId()), e);
        }
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
