package client;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Date;

public class Client {
    public static void offerNewRide(String name,
                            String phoneNumber,
                            String startCityName,
                            String endCityName,
                            String departureDate,
                            long vacancies,
                            long permittedDeviation
                            ){
        try{
            JSONObject data = new JSONObject();
            data.put("personName", name);
            data.put("phoneNumber", phoneNumber);
            data.put("startCityName", startCityName);
            data.put("endCityName", endCityName);
//             data.put("departureDate", departureDate);
            data.put("vacancies", Long.toString(vacancies));
            data.put("permittedDeviation", Long.toString(permittedDeviation));
            makeCall("http://localhost:8080/rideOffers", data, "POST");
        } catch (Exception e){}
        
    }

    public static void makeCall(String restUrl, JSONObject data, String callType) {
        try {

            // URL and parameters for the connection, This particulary returns the information passed
            URL url = new URL(restUrl);
            HttpURLConnection httpConnection  = (HttpURLConnection) url.openConnection();
            httpConnection.setDoOutput(true);
            httpConnection.setRequestMethod(callType);
            httpConnection.setRequestProperty("Content-Type", "application/json");
            httpConnection.setRequestProperty("Accept", "application/json");
            // Not required
            // urlConnection.setRequestProperty("Content-Length", String.valueOf(input.getBytes().length));

            // Writes the JSON parsed as string to the connection
            System.out.println(data.toString());
            DataOutputStream wr = new DataOutputStream(httpConnection.getOutputStream());
            wr.write(data.toString().getBytes());
            Integer responseCode = httpConnection.getResponseCode();

            BufferedReader bufferedReader;

            // Creates a reader buffer
            if (responseCode > 199 && responseCode < 300) {
                bufferedReader = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
            } else {
                bufferedReader = new BufferedReader(new InputStreamReader(httpConnection.getErrorStream()));
            }

            // To receive the response
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line).append("\n");
            }
            bufferedReader.close();

            // Prints the response
            System.out.println(content.toString());

        } catch (Exception e) {
            System.out.println("Error Message");
            System.out.println(e.getClass().getSimpleName());
            System.out.println(e.getMessage());
        }
    }


}
