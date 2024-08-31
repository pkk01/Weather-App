import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class WeatherApp {
    // fetch weather data from given location

    public static JSONObject getWeatherData(String locationName) {
        // using geolocation API getting the location coordinates
        JSONArray locationData = getLocationData(locationName);
        return null;
    }

    // retrieves gerographical coordinates for given location names
    public static JSONArray getLocationData(String locationName) {
        locationName = locationName.replaceAll(" ", "+");

        // build API URL with location parameter
        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name="
                + locationName + "&count=10&language=en&format=json";

        try {
            // call API to get response
            HttpURLConnection conn = fetchApiResponse(urlString);

            // check the response status --> 200 means sucessfully connected

            if (conn.getResponseCode() != 200) {
                System.out.println("Error: Could not connect to API");
                return null;

            } else {
                // Store the API
                StringBuilder resultJson = new StringBuilder();
                Scanner scanner = new Scanner(conn.getInputStream());

                // read and store the resulting json into string builder
                while (scanner.hasNext()) {
                    resultJson.append(scanner.nextLine());
                }

                scanner.close();
                conn.disconnect();

                // parse the JSON string into a JSON obj

                JSONParser parser = new JSONParser();
                JSONObject resultsJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));

                // get the list of location data the API generated from the location name
                JSONArray locationData = (JSONArray) resultsJsonObj.get("result");
                return locationData;

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static HttpURLConnection fetchApiResponse(String urlString) {
        try {
            // attempt to create connection
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // set request method to get
            conn.setRequestMethod("GET");

            // connet to API
            conn.connect();
            return conn;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null; // if not make connection
    }
}
