package com.bigdata.datashops.server.rpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bigdata.datashops.protocol.GrpcRequest;
import com.bigdata.datashops.protocol.RequestServiceGrpc;
import com.bigdata.datashops.server.master.processor.MasterGrpcProcessor;

import io.grpc.stub.StreamObserver;

@Service
public class MasterRequestServiceGrpcImpl extends RequestServiceGrpc.RequestServiceImplBase {
    private final Logger LOG = LoggerFactory.getLogger(MasterRequestServiceGrpcImpl.class);

    @Autowired
    private MasterGrpcProcessor masterGrpcProcessor;

    @Override
    public void send(GrpcRequest.Request request, StreamObserver<GrpcRequest.Response> responseObserver) {
        LOG.info("Grpc Receive request={}, from={}, type={}", request.getRequestId(), request.getIp(),
                request.getRequestType());
        // TODO
        GrpcRequest.RequestType type = request.getRequestType();
        switch (type) {
            case JOB_EXECUTE_RESPONSE:
                masterGrpcProcessor.processJobResponse(request);
                break;
            case JOB_KILL_RESPONSE:
                masterGrpcProcessor.processJobKill(request);
                break;
            case READ_WHOLE_LOG_RESPONSE:
                String content = request.getBody().toStringUtf8();
                break;
            case ROLL_READ_LOG_RESPONSE:
                break;
            case DELETE_LOG_RESPONSE:
                break;
            case HEART_BEAT:
                masterGrpcProcessor.processHeartBeat(request);
                break;
            default:
                break;
        }
        //        GrpcRequest.Response response = GrpcRequest.Response.newBuilder().setRequestId(request.getRequestId())
        //                                                .setCode(Constants.RPC_JOB_SUCCESS).setIp(NetUtils
        //                                                .getLocalAddress())
        //                                                .setPort(PropertyUtils.getInt(Constants
        //                                                .WORKER_GRPC_SERVER_PORT))
        //                                                .build();
        //        responseObserver.onNext(response);
        //        responseObserver.onCompleted();
    }
}
