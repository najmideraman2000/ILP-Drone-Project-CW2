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

public class Menus {

    private String machineName = new String("localhost");
    private String serverPort = new String("80");

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

    // Initialise new HttpClient and request menus.json from the web server
    private static final HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://" + machineName + ":" + serverPort + "/menus/menus.json")).build();

    /**
     * Method to return cost of a delivery given an order.
     *
     * @param items Strings that represent the name of items that are in the order.
     * @return the total cost of the order including the delivery cost.
     */
    public int getDeliveryCost(String... items) {
        int penceCost = 0;

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
            return penceCost;
        }

        return penceCost;
    }
}
