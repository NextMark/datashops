package com.bigdata.datashops.server.rpc;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bigdata.datashops.common.Constants;
import com.bigdata.datashops.common.utils.NetUtils;
import com.bigdata.datashops.protocol.GrpcRequest;
import com.bigdata.datashops.protocol.RequestServiceGrpc;
import com.bigdata.datashops.server.master.processor.MasterGrpcProcessor;
import com.bigdata.datashops.server.worker.processor.WorkerGrpcProcessor;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

@Service
public class GrpcRemotingServer {
    private final Logger LOG = LoggerFactory.getLogger(GrpcRemotingServer.class);

    private Server server;

    @Autowired
    private GrpcServerConfig grpcServerConfig;

    @Autowired
    private MasterGrpcProcessor masterGrpcProcessor;

    @Autowired
    private WorkerGrpcProcessor workerGrpcProcessor;

    public void start() throws IOException {
        LOG.info("Grpc server starting...");
        RequestServiceGrpcImpl requestServiceGrpc = new RequestServiceGrpcImpl();
        server = ServerBuilder.forPort(grpcServerConfig.getPort()).addService(requestServiceGrpc).build().start();
        LOG.info("Grpc server started, listening on {}", grpcServerConfig.getPort());
        Runtime.getRuntime().addShutdownHook(new Thread(GrpcRemotingServer.this::stop));
    }

    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    private void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    public class RequestServiceGrpcImpl extends RequestServiceGrpc.RequestServiceImplBase {
        @Override
        public void send(GrpcRequest.Request request, StreamObserver<GrpcRequest.Response> responseObserver) {
            LOG.info("[Grpc] receive request, rid {}, host {}, type {}", request.getRequestId(), request.getHost(),
                    request.getRequestType());
            // TODO
            GrpcRequest.RequestType type = request.getRequestType();
            switch (type) {
                case JOB_EXECUTE_REQUEST:
                    workerGrpcProcessor.processJobExec(request);
                    break;
                case JOB_EXECUTE_RESPONSE:
                    masterGrpcProcessor.processJobResponse(request);
                    break;
                case HEART_BEAT:
                    masterGrpcProcessor.processHeartBeat(request);
                    break;
                default:
                    break;
            }
            GrpcRequest.Response response = GrpcRequest.Response.newBuilder().setRequestId(request.getRequestId())
                                                    .setStatus(Constants.RPC_SUCCESS)
                                                    .setHost(NetUtils.getLocalAddress()).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

}
