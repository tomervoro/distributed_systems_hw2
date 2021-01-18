package client_grpc;

import generated.*;
import io.grpc.Channel;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import utils.RideOfferAlreadyExistsException;

import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.Map;

public class ServerCommClient {
    private static final Logger logger = Logger.getLogger(ServerCommClient.class.getName());

    private final ServerCommGrpc.ServerCommBlockingStub blockingStub;

    public ServerCommClient(Channel channel) {

        blockingStub = ServerCommGrpc.newBlockingStub(channel);
    }


    public boolean offerRide(RideOffer newRideOffer){
        try {
            ServerResponse response = blockingStub.offerRide(newRideOffer);
            if (response.getResult() == ServerResponse.Result.RIDE_ALREADY_EXISTS){
                throw new RideOfferAlreadyExistsException();
            }
            return true;
        }catch (StatusRuntimeException e){
            if (e.getStatus() == Status.ALREADY_EXISTS)
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public SnapshotInfo getSnapshot(){
        try {
            SnapshotInfo response = blockingStub.getSnapshot(Dummy.newBuilder().build());
            return response;
        }catch (StatusRuntimeException e){
            if (e.getStatus() == Status.ALREADY_EXISTS)
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public RideOffer askRide(RideRequest request){
        try {
            RideOffer response = blockingStub.askRide(request);
            if (response != null)
            {
                System.out.println("requested ride: " + request.toString() + "\ngot response: " + response.toString());
                return response;
            }
            else {
                System.out.println("No ride found!");
                return null;
            }
        } catch (StatusRuntimeException e) {

            if (e.getStatus() == Status.NOT_FOUND){
                return null;
            }else{
                throw e;
            }
        }
    }


    public void commitOrAbortRideRequest(RideRequest req) {
        blockingStub.commitOrAbortRideRequest(req);
    }
}
