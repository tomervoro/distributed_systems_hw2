package generated;

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
    value = "by gRPC proto compiler (version 1.33.0)",
    comments = "Source: scheme.proto")
public final class ServerCommGrpc {

  private ServerCommGrpc() {}

  public static final String SERVICE_NAME = "servercommunication.ServerComm";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<generated.RideOffer,
      generated.ServerResponse> getOfferRideMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "offerRide",
      requestType = generated.RideOffer.class,
      responseType = generated.ServerResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<generated.RideOffer,
      generated.ServerResponse> getOfferRideMethod() {
    io.grpc.MethodDescriptor<generated.RideOffer, generated.ServerResponse> getOfferRideMethod;
    if ((getOfferRideMethod = ServerCommGrpc.getOfferRideMethod) == null) {
      synchronized (ServerCommGrpc.class) {
        if ((getOfferRideMethod = ServerCommGrpc.getOfferRideMethod) == null) {
          ServerCommGrpc.getOfferRideMethod = getOfferRideMethod =
              io.grpc.MethodDescriptor.<generated.RideOffer, generated.ServerResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "offerRide"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  generated.RideOffer.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  generated.ServerResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ServerCommMethodDescriptorSupplier("offerRide"))
              .build();
        }
      }
    }
    return getOfferRideMethod;
  }

  private static volatile io.grpc.MethodDescriptor<generated.RideRequest,
      generated.RideOffer> getAskRideMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "askRide",
      requestType = generated.RideRequest.class,
      responseType = generated.RideOffer.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<generated.RideRequest,
      generated.RideOffer> getAskRideMethod() {
    io.grpc.MethodDescriptor<generated.RideRequest, generated.RideOffer> getAskRideMethod;
    if ((getAskRideMethod = ServerCommGrpc.getAskRideMethod) == null) {
      synchronized (ServerCommGrpc.class) {
        if ((getAskRideMethod = ServerCommGrpc.getAskRideMethod) == null) {
          ServerCommGrpc.getAskRideMethod = getAskRideMethod =
              io.grpc.MethodDescriptor.<generated.RideRequest, generated.RideOffer>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "askRide"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  generated.RideRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  generated.RideOffer.getDefaultInstance()))
              .setSchemaDescriptor(new ServerCommMethodDescriptorSupplier("askRide"))
              .build();
        }
      }
    }
    return getAskRideMethod;
  }

  private static volatile io.grpc.MethodDescriptor<generated.RideRequest,
      generated.ServerResponse> getCommitOrAbortRideRequestMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "commitOrAbortRideRequest",
      requestType = generated.RideRequest.class,
      responseType = generated.ServerResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<generated.RideRequest,
      generated.ServerResponse> getCommitOrAbortRideRequestMethod() {
    io.grpc.MethodDescriptor<generated.RideRequest, generated.ServerResponse> getCommitOrAbortRideRequestMethod;
    if ((getCommitOrAbortRideRequestMethod = ServerCommGrpc.getCommitOrAbortRideRequestMethod) == null) {
      synchronized (ServerCommGrpc.class) {
        if ((getCommitOrAbortRideRequestMethod = ServerCommGrpc.getCommitOrAbortRideRequestMethod) == null) {
          ServerCommGrpc.getCommitOrAbortRideRequestMethod = getCommitOrAbortRideRequestMethod =
              io.grpc.MethodDescriptor.<generated.RideRequest, generated.ServerResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "commitOrAbortRideRequest"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  generated.RideRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  generated.ServerResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ServerCommMethodDescriptorSupplier("commitOrAbortRideRequest"))
              .build();
        }
      }
    }
    return getCommitOrAbortRideRequestMethod;
  }

  private static volatile io.grpc.MethodDescriptor<generated.Dummy,
      generated.SnapshotInfo> getGetSnapshotMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "getSnapshot",
      requestType = generated.Dummy.class,
      responseType = generated.SnapshotInfo.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<generated.Dummy,
      generated.SnapshotInfo> getGetSnapshotMethod() {
    io.grpc.MethodDescriptor<generated.Dummy, generated.SnapshotInfo> getGetSnapshotMethod;
    if ((getGetSnapshotMethod = ServerCommGrpc.getGetSnapshotMethod) == null) {
      synchronized (ServerCommGrpc.class) {
        if ((getGetSnapshotMethod = ServerCommGrpc.getGetSnapshotMethod) == null) {
          ServerCommGrpc.getGetSnapshotMethod = getGetSnapshotMethod =
              io.grpc.MethodDescriptor.<generated.Dummy, generated.SnapshotInfo>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "getSnapshot"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  generated.Dummy.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  generated.SnapshotInfo.getDefaultInstance()))
              .setSchemaDescriptor(new ServerCommMethodDescriptorSupplier("getSnapshot"))
              .build();
        }
      }
    }
    return getGetSnapshotMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static ServerCommStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ServerCommStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ServerCommStub>() {
        @java.lang.Override
        public ServerCommStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ServerCommStub(channel, callOptions);
        }
      };
    return ServerCommStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static ServerCommBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ServerCommBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ServerCommBlockingStub>() {
        @java.lang.Override
        public ServerCommBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ServerCommBlockingStub(channel, callOptions);
        }
      };
    return ServerCommBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static ServerCommFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ServerCommFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ServerCommFutureStub>() {
        @java.lang.Override
        public ServerCommFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ServerCommFutureStub(channel, callOptions);
        }
      };
    return ServerCommFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class ServerCommImplBase implements io.grpc.BindableService {

    /**
     */
    public void offerRide(generated.RideOffer request,
        io.grpc.stub.StreamObserver<generated.ServerResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getOfferRideMethod(), responseObserver);
    }

    /**
     */
    public void askRide(generated.RideRequest request,
        io.grpc.stub.StreamObserver<generated.RideOffer> responseObserver) {
      asyncUnimplementedUnaryCall(getAskRideMethod(), responseObserver);
    }

    /**
     */
    public void commitOrAbortRideRequest(generated.RideRequest request,
        io.grpc.stub.StreamObserver<generated.ServerResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getCommitOrAbortRideRequestMethod(), responseObserver);
    }

    /**
     */
    public void getSnapshot(generated.Dummy request,
        io.grpc.stub.StreamObserver<generated.SnapshotInfo> responseObserver) {
      asyncUnimplementedUnaryCall(getGetSnapshotMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getOfferRideMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                generated.RideOffer,
                generated.ServerResponse>(
                  this, METHODID_OFFER_RIDE)))
          .addMethod(
            getAskRideMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                generated.RideRequest,
                generated.RideOffer>(
                  this, METHODID_ASK_RIDE)))
          .addMethod(
            getCommitOrAbortRideRequestMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                generated.RideRequest,
                generated.ServerResponse>(
                  this, METHODID_COMMIT_OR_ABORT_RIDE_REQUEST)))
          .addMethod(
            getGetSnapshotMethod(),
            asyncServerStreamingCall(
              new MethodHandlers<
                generated.Dummy,
                generated.SnapshotInfo>(
                  this, METHODID_GET_SNAPSHOT)))
          .build();
    }
  }

  /**
   */
  public static final class ServerCommStub extends io.grpc.stub.AbstractAsyncStub<ServerCommStub> {
    private ServerCommStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ServerCommStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ServerCommStub(channel, callOptions);
    }

    /**
     */
    public void offerRide(generated.RideOffer request,
        io.grpc.stub.StreamObserver<generated.ServerResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getOfferRideMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void askRide(generated.RideRequest request,
        io.grpc.stub.StreamObserver<generated.RideOffer> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getAskRideMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void commitOrAbortRideRequest(generated.RideRequest request,
        io.grpc.stub.StreamObserver<generated.ServerResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getCommitOrAbortRideRequestMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getSnapshot(generated.Dummy request,
        io.grpc.stub.StreamObserver<generated.SnapshotInfo> responseObserver) {
      asyncServerStreamingCall(
          getChannel().newCall(getGetSnapshotMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class ServerCommBlockingStub extends io.grpc.stub.AbstractBlockingStub<ServerCommBlockingStub> {
    private ServerCommBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ServerCommBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ServerCommBlockingStub(channel, callOptions);
    }

    /**
     */
    public generated.ServerResponse offerRide(generated.RideOffer request) {
      return blockingUnaryCall(
          getChannel(), getOfferRideMethod(), getCallOptions(), request);
    }

    /**
     */
    public generated.RideOffer askRide(generated.RideRequest request) {
      return blockingUnaryCall(
          getChannel(), getAskRideMethod(), getCallOptions(), request);
    }

    /**
     */
    public generated.ServerResponse commitOrAbortRideRequest(generated.RideRequest request) {
      return blockingUnaryCall(
          getChannel(), getCommitOrAbortRideRequestMethod(), getCallOptions(), request);
    }

    /**
     */
    public java.util.Iterator<generated.SnapshotInfo> getSnapshot(
        generated.Dummy request) {
      return blockingServerStreamingCall(
          getChannel(), getGetSnapshotMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class ServerCommFutureStub extends io.grpc.stub.AbstractFutureStub<ServerCommFutureStub> {
    private ServerCommFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ServerCommFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ServerCommFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<generated.ServerResponse> offerRide(
        generated.RideOffer request) {
      return futureUnaryCall(
          getChannel().newCall(getOfferRideMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<generated.RideOffer> askRide(
        generated.RideRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getAskRideMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<generated.ServerResponse> commitOrAbortRideRequest(
        generated.RideRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getCommitOrAbortRideRequestMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_OFFER_RIDE = 0;
  private static final int METHODID_ASK_RIDE = 1;
  private static final int METHODID_COMMIT_OR_ABORT_RIDE_REQUEST = 2;
  private static final int METHODID_GET_SNAPSHOT = 3;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final ServerCommImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(ServerCommImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_OFFER_RIDE:
          serviceImpl.offerRide((generated.RideOffer) request,
              (io.grpc.stub.StreamObserver<generated.ServerResponse>) responseObserver);
          break;
        case METHODID_ASK_RIDE:
          serviceImpl.askRide((generated.RideRequest) request,
              (io.grpc.stub.StreamObserver<generated.RideOffer>) responseObserver);
          break;
        case METHODID_COMMIT_OR_ABORT_RIDE_REQUEST:
          serviceImpl.commitOrAbortRideRequest((generated.RideRequest) request,
              (io.grpc.stub.StreamObserver<generated.ServerResponse>) responseObserver);
          break;
        case METHODID_GET_SNAPSHOT:
          serviceImpl.getSnapshot((generated.Dummy) request,
              (io.grpc.stub.StreamObserver<generated.SnapshotInfo>) responseObserver);
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

  private static abstract class ServerCommBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    ServerCommBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return generated.RouteGuideProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("ServerComm");
    }
  }

  private static final class ServerCommFileDescriptorSupplier
      extends ServerCommBaseDescriptorSupplier {
    ServerCommFileDescriptorSupplier() {}
  }

  private static final class ServerCommMethodDescriptorSupplier
      extends ServerCommBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    ServerCommMethodDescriptorSupplier(String methodName) {
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
      synchronized (ServerCommGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new ServerCommFileDescriptorSupplier())
              .addMethod(getOfferRideMethod())
              .addMethod(getAskRideMethod())
              .addMethod(getCommitOrAbortRideRequestMethod())
              .addMethod(getGetSnapshotMethod())
              .build();
        }
      }
    }
    return result;
  }
}
