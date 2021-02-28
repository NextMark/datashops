package com.bigdata.datashops.server.rpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bigdata.datashops.common.Constants;
import com.bigdata.datashops.common.utils.NetUtils;
import com.bigdata.datashops.protocol.GrpcRequest;
import com.bigdata.datashops.protocol.RequestServiceGrpc;
import com.bigdata.datashops.server.worker.processor.WorkerGrpcProcessor;

import io.grpc.stub.StreamObserver;

@Service
public class WorkerRequestServiceGrpcImpl extends RequestServiceGrpc.RequestServiceImplBase {
    private final Logger LOG = LoggerFactory.getLogger(MasterRequestServiceGrpcImpl.class);

    @Autowired
    private WorkerGrpcProcessor workerGrpcProcessor;

    @Override
    public void send(GrpcRequest.Request request, StreamObserver<GrpcRequest.Response> responseObserver) {
        LOG.info("Grpc Receive request {}, from: {}, type: {}", request.getRequestId(), request.getHost(),
                request.getRequestType());
        // TODO
        GrpcRequest.RequestType type = request.getRequestType();
        switch (type) {
            case JOB_EXECUTE_REQUEST:
                workerGrpcProcessor.processJobExec(request);
                break;
            default:
                break;
        }
        GrpcRequest.Response response = GrpcRequest.Response.newBuilder().setRequestId(request.getRequestId())
                                                .setStatus(Constants.RPC_JOB_SUCCESS)
                                                .setHost(NetUtils.getLocalAddress()).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
