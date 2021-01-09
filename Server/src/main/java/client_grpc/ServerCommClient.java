package client_grpc;

import generated.*;
import io.grpc.Channel;
import io.grpc.StatusRuntimeException;

import java.util.logging.Logger;

public class ServerCommClient {
    private static final Logger logger = Logger.getLogger(ServerCommClient.class.getName());

    private final ServerCommGrpc.ServerCommBlockingStub blockingStub;

    public ServerCommClient(Channel channel) {

        blockingStub = ServerCommGrpc.newBlockingStub(channel);
    }


    public void offerRide(RideOffer newRideOffer) {
        try {
            ServerResponse response = blockingStub.offerRide(newRideOffer);
            System.out.println("Added ride: " + newRideOffer.toString());
        } catch (StatusRuntimeException e) {
            e.printStackTrace();
        }
    }

    public void askRide(RideRequest request){
        
    }

//
//    public void routeChat(){
//        StreamObserver<RouteNote> requestObserver =
//                asyncStub.routeChat(new StreamObserver<RouteNote>() {
//                    @Override
//                    public void onNext(RouteNote note) {
//                        System.out.println(note.getMessage());
//                    }
//
//                    @Override
//                    public void onError(Throwable t) {
//                        System.out.println(t.getMessage());
//                    }
//
//                    @Override
//                    public void onCompleted() {
//                        System.out.println("Client.java side stream completed");
//                    }
//                });
//
//        try {
//            RouteNote[] requests =
//                    {
//                            newNote("First message", 0, 0),
//                            newNote("Second message", 0, 1),
//                            newNote("Third message", 1, 0),
//                            newNote("Fourth message", 1, 1)
//                    };
//
//            for (RouteNote request : requests) {
//                requestObserver.onNext(request);
//            }
//        } catch (RuntimeException e) {
//            // Cancel RPC
//            requestObserver.onError(e);
//            e.printStackTrace();
//        }
//        // Mark the end of requests
//        requestObserver.onCompleted();
//    }
//
//    private RouteNote newNote(String message, int lat, int lon) {
//        return RouteNote.newBuilder().setMessage(message)
//                .setLocation(Point.newBuilder().setLatitude(lat).setLongitude(lon).build()).build();
//    }
//
}
