package com.bigdata.datashops.server.rpc;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.bigdata.datashops.protocol.GrpcRequest;
import com.bigdata.datashops.protocol.RequestServiceGrpc;
import com.bigdata.datashops.remote.rpc.GrpcRemotingServer;
import com.google.common.collect.Lists;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

@SpringBootTest(classes = GrpcRemotingServer.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GrpcServerTest {
    @Autowired
    GrpcRemotingServer grpcRemotingServer;

    @Autowired
    WorkerRequestServiceGrpcImpl requestServiceGrpc;

    @Test
    public void main() throws IOException, InterruptedException {
        grpcRemotingServer.start(6000, requestServiceGrpc);

        Thread.sleep(10000);

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 60001).usePlaintext().build();
        RequestServiceGrpc.RequestServiceBlockingStub blockingStub = RequestServiceGrpc.newBlockingStub(channel);

        List<Integer> nums = Lists.newArrayList();
        for (Integer i = 101; i < 105; i++) {
            nums.add(i);
        }
        for (Integer num : nums) {
            Thread.sleep(1000);
            GrpcRequest.Request request = GrpcRequest.Request.newBuilder().setIp(num.toString()).build();
            GrpcRequest.Response response = blockingStub.send(request);
            System.out.println("resp " + response.getIp());
        }
        Thread.sleep(10000);
    }
}
