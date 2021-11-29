package uk.ac.ed.inf;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import java.util.ArrayList;
import java.util.HashMap;

public class Menus {
    private final String machineName;
    private final String serverPort;

    /**
     * Constructor for Menus class.
     *
     * @param machineName the name of the machine which the server running.
     * @param serverPort the port where the server running.
     */
    public Menus(String machineName, String serverPort) {
        this.machineName = machineName;
        this.serverPort = serverPort;
    }

    public static class Restaurant {
        String location;
        ArrayList<Item> menu;
    }

    public static class Item {
        String item;
        int pence;
    }

    // Initialise new HttpClient and request menus.json from the web server
    private static final HttpClient client = HttpClient.newHttpClient();

    /**
     * Method to return cost of a delivery given an order.
     *
     * @param items Strings that represent the name of items that are in the order.
     * @return the total cost of the order including the delivery cost.
     */
    public int getDeliveryCost(ArrayList<String> items) {
        int penceCost = 0;
        String urlString = "http://" + machineName + ":" + serverPort + "/menus/menus.json";
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(urlString)).build();

        try {
            // Send http request and using the response to makes an ArrayList of the restaurants containing the menu
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            Type listType = new TypeToken<ArrayList<Restaurant>>() {}.getType();
            ArrayList<Restaurant> restaurants = new Gson().fromJson(response.body(), listType);

            // Create an ArrayList containing all the items from all menus
            ArrayList<Item> listAllItems = new ArrayList<>();
            for (Restaurant restaurant : restaurants) {
                ArrayList<Item> currentMenu = restaurant.menu;
                listAllItems.addAll(currentMenu);
            }

            for (String item : items) {
                for (Item currentItem : listAllItems) {
                    if (item.equals(currentItem.item)) {
                        penceCost += currentItem.pence;
                    }
                }
            }
            penceCost += 50;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return penceCost;
    }

    /**
     * Method to get HashMap of String w3w : [item] for each restaurant
     * @return HashMap w3w : [item]
     */
    public HashMap<String,ArrayList<String>> getRestaurantItems() {
        String urlString = "http://" + machineName + ":" + serverPort + "/menus/menus.json";
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(urlString)).build();
        HashMap<String, ArrayList<String>> w3wItems = new HashMap<>();

        try {
            // Send http request and using the response to makes an ArrayList of the restaurants containing the menu
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            Type listType = new TypeToken<ArrayList<Restaurant>>() {}.getType();
            ArrayList<Restaurant> restaurants = new Gson().fromJson(response.body(), listType);

            for (Restaurant restaurant : restaurants) {
                ArrayList<String> itemsList = new ArrayList<>();
                ArrayList<Item> currentMenu = restaurant.menu;
                for (Item currentItem : currentMenu) {
                    itemsList.add(currentItem.item);
                }
                w3wItems.put(restaurant.location, itemsList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return w3wItems;
    }
}
