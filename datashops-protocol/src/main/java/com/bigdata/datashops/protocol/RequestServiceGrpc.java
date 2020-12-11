package com.bigdata.datashops.protocol;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.33.1)",
    comments = "Source: request.proto")
public final class RequestServiceGrpc {

  private RequestServiceGrpc() {}

  public static final String SERVICE_NAME = "RequestService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.bigdata.datashops.protocol.GrpcRequest.Request,
      com.bigdata.datashops.protocol.GrpcRequest.Response> getSendMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "send",
      requestType = com.bigdata.datashops.protocol.GrpcRequest.Request.class,
      responseType = com.bigdata.datashops.protocol.GrpcRequest.Response.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.bigdata.datashops.protocol.GrpcRequest.Request,
      com.bigdata.datashops.protocol.GrpcRequest.Response> getSendMethod() {
    io.grpc.MethodDescriptor<com.bigdata.datashops.protocol.GrpcRequest.Request, com.bigdata.datashops.protocol.GrpcRequest.Response> getSendMethod;
    if ((getSendMethod = RequestServiceGrpc.getSendMethod) == null) {
      synchronized (RequestServiceGrpc.class) {
        if ((getSendMethod = RequestServiceGrpc.getSendMethod) == null) {
          RequestServiceGrpc.getSendMethod = getSendMethod =
              io.grpc.MethodDescriptor.<com.bigdata.datashops.protocol.GrpcRequest.Request, com.bigdata.datashops.protocol.GrpcRequest.Response>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "send"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.bigdata.datashops.protocol.GrpcRequest.Request.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.bigdata.datashops.protocol.GrpcRequest.Response.getDefaultInstance()))
              .setSchemaDescriptor(new RequestServiceMethodDescriptorSupplier("send"))
              .build();
        }
      }
    }
    return getSendMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static RequestServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<RequestServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<RequestServiceStub>() {
        @java.lang.Override
        public RequestServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new RequestServiceStub(channel, callOptions);
        }
      };
    return RequestServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static RequestServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<RequestServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<RequestServiceBlockingStub>() {
        @java.lang.Override
        public RequestServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new RequestServiceBlockingStub(channel, callOptions);
        }
      };
    return RequestServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static RequestServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<RequestServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<RequestServiceFutureStub>() {
        @java.lang.Override
        public RequestServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new RequestServiceFutureStub(channel, callOptions);
        }
      };
    return RequestServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class RequestServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void send(com.bigdata.datashops.protocol.GrpcRequest.Request request,
        io.grpc.stub.StreamObserver<com.bigdata.datashops.protocol.GrpcRequest.Response> responseObserver) {
      asyncUnimplementedUnaryCall(getSendMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getSendMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.bigdata.datashops.protocol.GrpcRequest.Request,
                com.bigdata.datashops.protocol.GrpcRequest.Response>(
                  this, METHODID_SEND)))
          .build();
    }
  }

  /**
   */
  public static final class RequestServiceStub extends io.grpc.stub.AbstractAsyncStub<RequestServiceStub> {
    private RequestServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected RequestServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new RequestServiceStub(channel, callOptions);
    }

    /**
     */
    public void send(com.bigdata.datashops.protocol.GrpcRequest.Request request,
        io.grpc.stub.StreamObserver<com.bigdata.datashops.protocol.GrpcRequest.Response> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getSendMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class RequestServiceBlockingStub extends io.grpc.stub.AbstractBlockingStub<RequestServiceBlockingStub> {
    private RequestServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected RequestServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new RequestServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.bigdata.datashops.protocol.GrpcRequest.Response send(com.bigdata.datashops.protocol.GrpcRequest.Request request) {
      return blockingUnaryCall(
          getChannel(), getSendMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class RequestServiceFutureStub extends io.grpc.stub.AbstractFutureStub<RequestServiceFutureStub> {
    private RequestServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected RequestServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new RequestServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.bigdata.datashops.protocol.GrpcRequest.Response> send(
        com.bigdata.datashops.protocol.GrpcRequest.Request request) {
      return futureUnaryCall(
          getChannel().newCall(getSendMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_SEND = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final RequestServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(RequestServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_SEND:
          serviceImpl.send((com.bigdata.datashops.protocol.GrpcRequest.Request) request,
              (io.grpc.stub.StreamObserver<com.bigdata.datashops.protocol.GrpcRequest.Response>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class RequestServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    RequestServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.bigdata.datashops.protocol.GrpcRequest.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("RequestService");
    }
  }

  private static final class RequestServiceFileDescriptorSupplier
      extends RequestServiceBaseDescriptorSupplier {
    RequestServiceFileDescriptorSupplier() {}
  }

  private static final class RequestServiceMethodDescriptorSupplier
      extends RequestServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    RequestServiceMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (RequestServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new RequestServiceFileDescriptorSupplier())
              .addMethod(getSendMethod())
              .build();
        }
      }
    }
    return result;
  }
}
