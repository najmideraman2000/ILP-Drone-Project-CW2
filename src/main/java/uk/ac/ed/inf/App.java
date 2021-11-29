package uk.ac.ed.inf;

import com.mapbox.geojson.*;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

public class App {

    public static void main(String[] args) {
//        String day = args[0];
//        String month = args[1];
//        String year = args[2];
//        String serverPort = args[3];
//        String databasePort = args[4];

        String day = "12";
        String month = "12";
        String year = "2023";
        String serverPort = "80";
        String databasePort = "1527";

        // Create all final objects
        Database dataset = new Database("localhost", day, month, year, databasePort);
        Buildings buildings = new Buildings("localhost", serverPort);
        Menus menus = new Menus("localhost", serverPort);
        Drone drone = new Drone(1500, -3.186874, 55.944494);

        // Get no-fly zones as a list of mapbox.geojson.Polygon object
        final ArrayList<Polygon> noFlyZones = buildings.getNoFlyZones();

        // Get the HashMap of w3w:[item] for each w3w of restaurant
        HashMap<String, ArrayList<String>> restaurantW3WItems = menus.getRestaurantItems();

        // Get list of all required info from the Database object
        ArrayList<ArrayList<String>> infoList = dataset.getDatabaseInfo();
        ArrayList<String> orderNoList = infoList.get(0);
        ArrayList<String> deliverToList = infoList.get(1);
        ArrayList<String> itemList = infoList.get(2);

        // Create a list of unique orderNo
        Set<String> set = new LinkedHashSet<>(orderNoList);
        ArrayList<String> uniqueOrderList = new ArrayList<>(set);

        // Create HashMap of orderNo and for each deliveryTo, items, deliveryCost
        HashMap<String, String> orderDeliveryTo = HelperMethods.getOrderDeliveryTo(uniqueOrderList, orderNoList, deliverToList);
        HashMap<String, ArrayList<String>> orderItems = HelperMethods.getOrderItems(uniqueOrderList, orderNoList, itemList);
        HashMap<String, Integer> orderDeliveryCost = HelperMethods.getOrderDeliveryCost(orderItems, menus);

        // Sort the HashMap orderNo:deliveryCost by value (greedy approach)
        HashMap<String, Integer> orderDeliveryCostSorted = HelperMethods.sortHashMap(orderDeliveryCost, false);

        //////////////////////////////////////////////////////////////////////

        // Initiate lists to be used to create tables at database
        List<List<Flightpath>> flightPaths = new ArrayList<>();
        List<Deliveries> deliveries = new ArrayList<>();

        // Initiate list to be used to create a geojson file
        List<List<Point>> allPointList = new ArrayList<>();

        // Boolean which indicates whether the drone is already at base (e.g. in case the drone run out of moves)
        boolean alreadyHome = false;

        // Initiate Appleton Tower LongLat for convenience
        LongLat appletonTower = new LongLat(-3.186874, 55.944494);

        // Initialise a grid map of all points (0 for available grid and -1 for unavailable grid)
        int[][] map = HelperMethods.makeAMap(noFlyZones);

        //////////////////////////////////////////////////////////////////////

        // Loop through orderNo (that is already sorted by deliveryCost)
        for (HashMap.Entry<String, Integer> entry : orderDeliveryCostSorted.entrySet()) {
            Deliveries newDelivery = new Deliveries(entry.getKey(), orderDeliveryTo.get(entry.getKey()), entry.getValue());
            deliveries.add(newDelivery);
            ArrayList<LongLat> currentLongLatList = new ArrayList<>();

            // Add LongLat of shops for current orderNo to currentLongLatList
            ArrayList<String> items = orderItems.get(entry.getKey());
            for (String item : items) {
                for (HashMap.Entry<String, ArrayList<String>> entry2 : restaurantW3WItems.entrySet()) {
                    ArrayList<String> currentRestaurantMenu = entry2.getValue();
                    if (currentRestaurantMenu.contains(item)) {
                        String restaurantW3W = entry2.getKey();
                        LongLat restaurantLongLat = HelperMethods.getLongLatFromW3W(restaurantW3W, "localhost", serverPort);
                        currentLongLatList.add(restaurantLongLat);
                        break;
                    }
                }
            }

            // Add LongLat of deliveryTo for current orderNo to currentLongLatList
            String deliverTo = orderDeliveryTo.get(entry.getKey());
            LongLat deliverToLongLat = HelperMethods.getLongLatFromW3W(deliverTo, "localhost", serverPort);
            currentLongLatList.add(deliverToLongLat);


            // Get the number of available moves before drone making move and verify with movesRequired which will increment
            int availableDroneMoves = drone.getAvailableMoves();
            int movesRequired = 0;
            LongLat initialPosition = HelperMethods.getLongLatFromDoubles(drone.getDroneLongitude(), drone.getDroneLatitude());

            // Initialise lise of Flightpath for database and pointList for geojson file
            List<Flightpath> currentFlightpath = new ArrayList<>();
            List<Point> pointList = new ArrayList<>();

            // Loop through list of all restaurants and ended with deliveryTo LongLat (for an order)
            for (LongLat destination : currentLongLatList) {

                // Find all move nodes through pathfinding algorithm
                List<PathfindingAlgorithm.Node> moveNodes = HelperMethods.getMoveNodes(drone, destination, map);

                if (moveNodes == null) {
                    drone.setAvailableMoves(drone.getAvailableMoves() - 1);
                    Point newPoint = Point.fromLngLat(drone.getDroneLongitude(), drone.getDroneLatitude());
                    pointList.add(newPoint);
                    break;
                }

                // Loop through all move node (for travel from one place to another one)
                for (PathfindingAlgorithm.Node moveNode : moveNodes) {
                    Flightpath currentPath = HelperMethods.getFlightpath(entry.getKey(), drone, moveNode.x, moveNode.y);
                    currentFlightpath.add(currentPath);

                    movesRequired += 1;
                    drone.setAvailableMoves(drone.getAvailableMoves() - 1);
                    drone.setDroneLongLat(moveNode.x, moveNode.y);
                    Point newPoint = Point.fromLngLat(moveNode.x, moveNode.y);
                    pointList.add(newPoint);

                    if (drone.getDroneLongLat().closeTo(destination)) {
                        Flightpath hover = HelperMethods.getFlightpathHover(entry.getKey(), moveNode.x, moveNode.y);
                        currentFlightpath.add(hover);

                        movesRequired += 1;
                        drone.setAvailableMoves(drone.getAvailableMoves() - 1);
                        pointList.add(newPoint);
                        break;
                    }
                }
            }

            int movesToAppletonTower = HelperMethods.movesToAppletonTower(drone, appletonTower, map);
            movesRequired += movesToAppletonTower;

            // Check if the move that just made exceeds drone available moves. If it is, reset the drone to initial position
            // and calculate the  move from initial position to Appleton Tower
            if (movesRequired > availableDroneMoves) {
                deliveries.remove(deliveries.size()-1);
                pointList.clear();
                currentFlightpath.clear();
                alreadyHome = true;
                drone.setDroneLongLat(initialPosition.getLongitude(), initialPosition.getLatitude());
                List<PathfindingAlgorithm.Node> moveNodes = HelperMethods.getMoveNodes(drone, appletonTower, map);

                if (moveNodes == null) {
                    Point newPoint = Point.fromLngLat(drone.getDroneLongitude(), drone.getDroneLatitude());
                    pointList.add(newPoint);
                    break;
                }

                // Find the path to appleton tower and update the database and point lists
                for (PathfindingAlgorithm.Node moveNode : moveNodes) {
                    Flightpath currentPath = HelperMethods.getFlightpath(entry.getKey(), drone, moveNode.x, moveNode.y);
                    currentFlightpath.add(currentPath);

                    drone.setDroneLongLat(moveNode.x, moveNode.y);
                    Point newPoint = Point.fromLngLat(moveNode.x, moveNode.y);
                    pointList.add(newPoint);

                    if (drone.getDroneLongLat().closeTo(appletonTower)) {
                        Flightpath hover = HelperMethods.getFlightpathHover(entry.getKey(), moveNode.x, moveNode.y);
                        currentFlightpath.add(hover);
                        pointList.add(newPoint);
                        break;
                    }
                }
                allPointList.add(pointList);
                flightPaths.add(currentFlightpath);
                break;
            }
            // Add the database and point list for current order
            allPointList.add(pointList);
            flightPaths.add(currentFlightpath);
        }

        // If the drone still is not at base after all delivery, go back to appleton tower.
        if (!alreadyHome) {
            List<Flightpath> currentFlightpath = new ArrayList<>();
            ArrayList<Point> pointList = new ArrayList<>();
            List<PathfindingAlgorithm.Node> moveNodes = HelperMethods.getMoveNodes(drone, appletonTower, map);

            assert moveNodes != null;
            for (PathfindingAlgorithm.Node moveNode : moveNodes) {
                Flightpath currentPath = HelperMethods.getFlightpath("done", drone, moveNode.x, moveNode.y);
                currentFlightpath.add(currentPath);

                drone.setDroneLongLat(moveNode.x, moveNode.y);
                Point newPoint = Point.fromLngLat(moveNode.x, moveNode.y);
                pointList.add(newPoint);

                if (drone.getDroneLongLat().closeTo(appletonTower)) {
                    Flightpath hover = HelperMethods.getFlightpathHover("done", moveNode.x, moveNode.y);
                    currentFlightpath.add(hover);
                    pointList.add(newPoint);
                    break;
                }
            }
            allPointList.add(pointList);
            flightPaths.add(currentFlightpath);
        }

        // Create a table called deliveries on database
        dataset.createDeliveriesTable(deliveries);

        // Flattened list of Flightpath objects. Create a table called flightpath on the database
        List<Flightpath> flattenedFlightpath = flightPaths.stream().flatMap(List::stream).collect(Collectors.toList());
        dataset.createFlightpathTable(flattenedFlightpath);

        // Flattened list of Point. Transform the list to a FeatureCollection object consist of only one LineString
        List<Point> flattenedPointLists = allPointList.stream().flatMap(List::stream).collect(Collectors.toList());
        LineString ls = LineString.fromLngLats(flattenedPointLists);
        Feature f = Feature.fromGeometry((Geometry)ls);
        FeatureCollection fc = FeatureCollection.fromFeature(f);
        String geoJsonFile = fc.toJson();

        // Write the Json to a file
        try (PrintWriter out = new PrintWriter("drone-" + day + "-" + month + "-" + year + ".geojson")) {
            out.println(geoJsonFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
