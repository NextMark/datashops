package com.bigdata.datashops.rpc;

import java.io.IOException;
import java.util.List;

import com.bigdata.datashops.protocol.GrpcRequest;
import com.bigdata.datashops.protocol.RequestServiceGrpc;
import com.google.common.collect.Lists;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GrpcClientTest {
    public static void main(String[] args) throws IOException, InterruptedException {
        GrpcServerConfig grpcServerConfig = new GrpcServerConfig();
        grpcServerConfig.setPort(60001);
        GrpcRemotingServer grpcRemotingServer = new GrpcRemotingServer(grpcServerConfig);
        grpcRemotingServer.start();

        Thread.sleep(10000);

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 60000).usePlaintext().build();
        RequestServiceGrpc.RequestServiceBlockingStub blockingStub = RequestServiceGrpc.newBlockingStub(channel);


        List<Integer> nums = Lists.newArrayList();
        for(Integer i=1; i<5; i++){
            nums.add(i);
        }
        for(Integer num: nums){
            Thread.sleep(1000);
            GrpcRequest.Request request = GrpcRequest.Request.newBuilder().setHost(num.toString()).build();
            GrpcRequest.Response response = blockingStub.send(request);
            System.out.println("resp " + response.getHost());
        }
        Thread.sleep(10000);
    }
}
