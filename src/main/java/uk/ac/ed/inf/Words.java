package uk.ac.ed.inf;

import com.google.gson.Gson;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

public class Words {
    private final String firstWord;
    private final String secondWord;
    private final String thirdWord;
    private final String machineName;
    private final String serverPort;

    public Words(String firstWord, String secondWord,String thirdWord, String machineName, String serverPort) {
        this.firstWord = firstWord;
        this.secondWord = secondWord;
        this.thirdWord = thirdWord;
        this.machineName = machineName;
        this.serverPort = serverPort;
    }

    public static class Location {
        Coordinates coordinates;
    }

    public static class Coordinates {
        double lng;
        double lat;
    }

    private static final HttpClient client = HttpClient.newHttpClient();

    /**
     * Method to get LongLat object given the w3w formatted String
     * @return LongLat object
     */
    public LongLat getLongLat() {
        double longitude = 0;
        double latitude = 0;
        String urlString = "http://" + machineName + ":" + serverPort + "/words/" + firstWord + "/" + secondWord + "/" + thirdWord + "/details.json";
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(urlString)).build();

        try {
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            Location location = new Gson().fromJson(response.body(), Location.class);
            Coordinates coordinates = location.coordinates;
            longitude = coordinates.lng;
            latitude = coordinates.lat;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new LongLat(longitude, latitude);
    }
}
