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
     * Constructor for Menus class
     *
     * @param machineName the name of the machine which the server running
     * @param serverPort the port where the server running
     */
    public Menus(String machineName, String serverPort) {
        this.machineName = machineName;
        this.serverPort = serverPort;
    }

    private static final HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://" + machineName + ":" + serverPort + "/menus/menus.json")).build();

    /**
     * Method to return cost of a delivery given an order
     *
     * @param items Strings that represent the name of item that are in the order
     * @return the total cost of the order including the delivery cost
     */
    public int getDeliveryCost(String... items) {
        int penceCost = 0;

        try {
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            Type listType = new TypeToken<ArrayList<Restaurant>>() {}.getType();
            ArrayList<Restaurant> restaurants = new Gson().fromJson(response.body(), listType);
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

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

        return penceCost + 50;
    }
}
