import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class WeatherApp {
    // fetch weather data from given location

    public static JSONObject getWeatherData(String locationName) {
        JSONArray locationData = getLocationData(locationName);

        if (locationData == null || locationData.isEmpty()) {
            System.out.println("Error: Location data is not available.");
            return null;
        }

        JSONObject location = (JSONObject) locationData.get(0);
        double latitude = (double) location.get("latitude");
        double longitude = (double) location.get("longitude");

        String urlString = "https://api.open-meteo.com/v1/forecast?" +
                "latitude=" + latitude + "&longitude=" + longitude +
                "&hourly=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m&timezone=auto";

        try {
            HttpURLConnection conn = fetchApiResponse(urlString);

            if (conn == null || conn.getResponseCode() != 200) {
                System.out.println("Error: Could not connect to API.");
                return null;
            }

            StringBuilder resultJson = new StringBuilder();
            Scanner scanner = new Scanner(conn.getInputStream());
            while (scanner.hasNext()) {
                resultJson.append(scanner.nextLine());
            }
            scanner.close();
            conn.disconnect();

            JSONParser parser = new JSONParser();
            JSONObject resultJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));
            JSONObject hourly = (JSONObject) resultJsonObj.get("hourly");

            if (hourly == null) {
                System.out.println("Error: Hourly weather data is not available.");
                return null;
            }

            JSONArray time = (JSONArray) hourly.get("time");
            int index = findIndexOfCurrentTime(time);

            // Retrieve temperature
            JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");
            Double temperature = temperatureData != null ? (Double) temperatureData.get(index) : null;

            // Retrieve weather code
            JSONArray weatherCode = (JSONArray) hourly.get("weather_code");
            String weatherCondition = weatherCode != null ? convertWeatherCode((long) weatherCode.get(index))
                    : "Unknown";

            // Retrieve humidity
            JSONArray relativeHumidity = (JSONArray) hourly.get("relative_humidity_2m");
            Long humidity = relativeHumidity != null ? (Long) relativeHumidity.get(index) : null;

            // Retrieve wind speed
            JSONArray windspeedData = (JSONArray) hourly.get("wind_speed_10m");
            Double windspeed = windspeedData != null ? (Double) windspeedData.get(index) : null;

            JSONObject weatherData = new JSONObject();
            weatherData.put("temperature", temperature != null ? temperature : "N/A");
            weatherData.put("weather_condition", weatherCondition);
            weatherData.put("humidity", humidity != null ? humidity : "N/A");
            weatherData.put("windspeed", windspeed != null ? windspeed : "N/A");

            return weatherData;

        } catch (Exception e) {
            e.printStackTrace();
        }
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
                JSONArray locationData = (JSONArray) resultsJsonObj.get("results");
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

    private static int findIndexOfCurrentTime(JSONArray timeList) {
        String currentTime = getCurrentTime();

        // iterate through the time list and see which one matches our current time
        for (int i = 0; i < timeList.size(); i++) {
            String time = (String) timeList.get(i);
            if (time.equalsIgnoreCase(currentTime))
                // return index
                return i;
        }
        return 0;
    }

    public static String getCurrentTime() {
        LocalDateTime currentDateTime = LocalDateTime.now();

        // format data to be 2024-09-02 --according to the API

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");

        // format and print the current data and time
        String formattedDateTime = currentDateTime.format(formatter);
        return formattedDateTime;

    }

    // convert the weather code to something more readable
    private static String convertWeatherCode(long weathercode) {
        String weatherCondition = "";
        if (weathercode == 0L) {
            weatherCondition = "Clear";
        } else if (weathercode > 0L && weathercode <= 3L) {
            weatherCondition = "Cloudy";
        } else if ((weathercode >= 51L && weathercode <= 67L) || (weathercode >= 80L && weathercode <= 99L)) {
            weatherCondition = "Rain";
        } else if (weathercode >= 71L && weathercode <= 77L) {
            weatherCondition = "Snow";
        }
        return weatherCondition;
    }
}
