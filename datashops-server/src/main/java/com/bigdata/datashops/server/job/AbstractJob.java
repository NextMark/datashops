package com.bigdata.datashops.server.job;

import static com.bigdata.datashops.model.enums.DbType.HIVE;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;

import com.bigdata.datashops.common.Constants;
import com.bigdata.datashops.dao.datasource.BaseDataSource;
import com.bigdata.datashops.model.pojo.job.JobInstance;
import com.bigdata.datashops.model.pojo.rpc.Host;
import com.bigdata.datashops.protocol.GrpcRequest;
import com.bigdata.datashops.server.master.dispatch.selector.RandomHostSelector;
import com.bigdata.datashops.server.rpc.GrpcRemotingClient;
import com.bigdata.datashops.server.utils.ZKUtils;
import com.bigdata.datashops.server.zookeeper.ZookeeperOperator;
import com.google.common.collect.Lists;

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

    public AbstractJob(JobInstance instance, Logger logger, GrpcRemotingClient grpcRemotingClient,
                       ZookeeperOperator zookeeperOperator) {
        this.instance = instance;
        this.LOG = logger;
        this.grpcRemotingClient = grpcRemotingClient;
        this.zookeeperOperator = zookeeperOperator;
    }

    public void execute() throws Exception {
        before();
        handle();
        after();
    }

    protected void before() {
        result.setInstanceId(instance.getInstanceId());
    }

    protected abstract void handle() throws Exception;

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
}
