package com.bigdata.datashops.server.job;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;

import com.bigdata.datashops.common.Constants;
import com.bigdata.datashops.common.utils.JSONUtils;
import com.bigdata.datashops.common.utils.NetUtils;
import com.bigdata.datashops.dao.datasource.DataSourceFactory;
import com.bigdata.datashops.dao.datasource.MySQLDataSource;
import com.bigdata.datashops.model.pojo.job.JobInstance;
import com.bigdata.datashops.protocol.GrpcRequest;
import com.bigdata.datashops.server.rpc.GrpcRemotingClient;
import com.bigdata.datashops.server.zookeeper.ZookeeperOperator;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.protobuf.ByteString;

public class MysqlJob extends AbstractJob {
    private static final int LIMIT = 10000;

    public MysqlJob(JobInstance instance, Logger logger, GrpcRemotingClient grpcRemotingClient,
                    ZookeeperOperator zookeeperOperator) {
        super(instance, logger, grpcRemotingClient, zookeeperOperator);
    }

    @Override
    protected void handle() throws Exception {
        String data = instance.getData();

        baseDataSource = JSONUtils.parseObject(data, MySQLDataSource.class);
        DataSourceFactory.loadClass(baseDataSource.dbType());

        try {
            Connection connection = creatConnection();
            PreparedStatement ps = connection.prepareStatement(baseDataSource.getData());
            ResultSet rs = ps.executeQuery();
            resultProcess(rs);
        } catch (SQLException e) {
            request = GrpcRequest.Request.newBuilder().setHost(NetUtils.getLocalAddress())
                              .setRequestId(RandomUtils.nextInt())
                              .setRequestType(GrpcRequest.RequestType.JOB_EXECUTE_RESPONSE)
                              .setCode(Constants.RPC_JOB_FAIL)
                              .setBody(ByteString.copyFrom(JSONUtils.toJsonString(e.getMessage()).getBytes())).build();
            e.printStackTrace();
        }
    }

    @Override
    public void after() {
        LOG.info("Job end");
        request =
                GrpcRequest.Request.newBuilder().setHost(NetUtils.getLocalAddress()).setRequestId(RandomUtils.nextInt())
                        .setRequestType(GrpcRequest.RequestType.JOB_EXECUTE_RESPONSE).setCode(Constants.RPC_JOB_SUCCESS)
                        .setBody(ByteString.copyFrom(JSONUtils.toJsonString(result).getBytes())).build();
        grpcRemotingClient.send(request, selectHost());
    }

    private void resultProcess(ResultSet resultSet) throws SQLException {
        ArrayNode resultJSONArray = JSONUtils.createArrayNode();
        ResultSetMetaData md = resultSet.getMetaData();
        int num = md.getColumnCount();

        int rowCount = 0;

        while (rowCount < LIMIT && resultSet.next()) {
            ObjectNode mapOfColValues = JSONUtils.createObjectNode();
            for (int i = 1; i <= num; i++) {
                mapOfColValues.set(md.getColumnLabel(i), JSONUtils.toJsonNode(resultSet.getObject(i)));
            }
            resultJSONArray.add(mapOfColValues);
            rowCount++;
        }
        result.setData(JSONUtils.toJsonString(resultJSONArray));
        LOG.info("execute sql : {}", result);
    }
}
