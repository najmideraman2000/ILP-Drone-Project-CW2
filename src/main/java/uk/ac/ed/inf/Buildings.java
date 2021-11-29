package uk.ac.ed.inf;

import com.google.gson.Gson;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Polygon;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.List;

public class Buildings {
    private final String machineName;
    private final String serverPort;

    public Buildings(String machineName, String serverPort) {
        this.machineName = machineName;
        this.serverPort = serverPort;
    }

    public static class LandmarkCollection {
        List<Landmark> features;
    }

    public static class Landmark {
        Details properties;
    }

    public static class Details {
        String location;
    }

    private static final HttpClient client = HttpClient.newHttpClient();

    /**
     * Method to get ArrayList of com.mapbox.geojson.Polygon of no-fly zones
     * @return ArrayList of Polygon
     */
    public ArrayList<Polygon> getNoFlyZones() {
        String urlString = "http://" + machineName + ":" + serverPort + "/buildings/no-fly-zones.geojson";
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(urlString)).build();
        ArrayList<Polygon> polygonList = new ArrayList<>();

        try {
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            String responseJson = response.body();
            FeatureCollection fc = FeatureCollection.fromJson(responseJson);
            List<Feature> features = fc.features();

            assert features != null;
            for (Feature feature : features) {
                polygonList.add((Polygon) (feature.geometry()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return polygonList;
    }

//    public List<LongLat> getLandmarksLongLat() {
//        String urlString = "http://" + machineName + ":" + serverPort + "/buildings/landmarks.geojson";
//        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(urlString)).build();
//        ArrayList<LongLat> longLatList = new ArrayList<>();
//
//        try {
//            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
//            LandmarkCollection landmarks = new Gson().fromJson(response.body(), LandmarkCollection.class);
//            List<Landmark> landmarkList = landmarks.features;
//
//            for (Landmark landmark : landmarkList) {
//                String landmarkW3W = landmark.properties.location;
//                LongLat landmarkLongLat = HelperMethods.getLongLatFromW3W(landmarkW3W, "localhost", serverPort);
//                longLatList.add(landmarkLongLat);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return longLatList;
//    }
}
