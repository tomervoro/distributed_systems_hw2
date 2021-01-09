import java.time.format.DateTimeFormatter;
import java.util.*;
import client.Client;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import com.google.protobuf.Timestamp;

public class main {
    public static void main(String args[]) throws InvalidProtocolBufferException {
        Date today = Calendar.getInstance().getTime();
        Timestamp timestamp = Timestamp.newBuilder()
                                        .setSeconds((int)(today.getTime() / 1000))
                                        .build();
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        String formattedDate = formatter.format(LocalDate.now());

        System.out.println("Sending...");
        Client.offerNewRide("Tomer Vor",
                            "05498343",
                            "TLV",
                            "Netanya",
//                            JsonFormat.printer().print(timestamp),
                                "",
                            1,
                            1);
        System.out.println("Sent");
    }
}
