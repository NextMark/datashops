package com.bigdata.datashops.server.rpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        LOG.info("Grpc receive request={}, from={}, type={}", request.getRequestId(), request.getIp(),
                request.getRequestType());
        // TODO
        GrpcRequest.RequestType type = request.getRequestType();
        GrpcRequest.Response response = null;
        switch (type) {
            case JOB_EXECUTE_REQUEST:
                workerGrpcProcessor.processJobExec(request);
                break;
            case JOB_KILL_REQUEST:
                break;
            case ROLL_READ_LOG_REQUEST:
                response = workerGrpcProcessor.rollReadLogRequest(request);
                break;
            case READ_WHOLE_LOG_REQUEST:
                response = workerGrpcProcessor.readWholeLogRequest(request);
                break;
            case DELETE_LOG_REQUEST:
                break;
            default:
                break;
        }
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
