package uk.ac.ed.inf;

import com.mapbox.geojson.Polygon;

import java.util.*;

public class HelperMethods {
    public static final double MIN_LONGITUDE = -3.192473;
    public static final double MAX_LONGITUDE = -3.184319;
    public static final double MIN_LATITUDE = 55.942617;
    public static final double MAX_LATITUDE = 55.946233;

    /**
     * Method to create a hash map of orderNo as the key and deliveryTo as the value
     * @param uniqueOrderList List of unique orderNo
     * @param orderNoList List of all orderNo which may contain duplicates
     * @param deliveryToList List of all deliveryTo which may contain duplicates and correspond to orderNo
     * @return HashMap of orderNo : deliveryTo
     */
    public static HashMap<String,String> getOrderDeliveryTo(ArrayList<String> uniqueOrderList, ArrayList<String> orderNoList, ArrayList<String> deliveryToList) {
        HashMap<String, String> orderDeliveryTo = new HashMap<>();
        for (String uniqueOrderNo : uniqueOrderList) {
            for (int i = 0; i < orderNoList.size(); i++) {
                if (orderNoList.get(i).equals(uniqueOrderNo)) {
                    orderDeliveryTo.put(uniqueOrderNo, deliveryToList.get(i));
                }
            }
        }
        return orderDeliveryTo;
    }

    /**
     * Method to create a hash map of orderNo as the key and List of item as the value
     * @param uniqueOrderList List of unique orderNo
     * @param orderNoList List of all orderNo which may contain duplicates
     * @param itemList List of all item corresponding to orderNo
     * @return HashMap of orderNo : [item]
     */
    public static HashMap<String,ArrayList<String>> getOrderItems(ArrayList<String> uniqueOrderList, ArrayList<String> orderNoList, ArrayList<String> itemList) {
        HashMap<String, ArrayList<String>> orderItems = new HashMap<>();
        for (String uniqueOrderNo : uniqueOrderList) {
            ArrayList<String> currentOrderItems = new ArrayList<>();
            for (int i = 0; i < orderNoList.size(); i++) {
                if (orderNoList.get(i).equals(uniqueOrderNo)) {
                    currentOrderItems.add(itemList.get(i));
                }
            }
            orderItems.put(uniqueOrderNo, currentOrderItems);
        }
        return orderItems;
    }

    /**
     * Method to create a hash map of orderNo as the key and deliveryCost of the order as the value
     * @param orderItems HashMap of order as the key and list of item as the value
     * @param menu Menus class
     * @return HashMap of orderNo : orderDeliveryCost
     */
    public static HashMap<String,Integer> getOrderDeliveryCost(HashMap<String,ArrayList<String>> orderItems, Menus menu) {
        HashMap<String, Integer> orderDeliveryCost = new HashMap<>();
        for (HashMap.Entry<String, ArrayList<String>> entry : orderItems.entrySet()) {
            String orderNo = entry.getKey();
            ArrayList<String> items = entry.getValue();
            int currentDeliveryCost = menu.getDeliveryCost(items);
            orderDeliveryCost.put(orderNo, currentDeliveryCost);
        }
        return orderDeliveryCost;
    }

    /**
     * Method to sort a HashMap given that the value is of Integer type
     * @param unsortedMap HashMap of unsorted HashMap
     * @param order Boolean to determine whether to sort increasingly or decreasingly.
     *              'true' to sort increasingly, 'false' to sort decreasingly
     * @return sorted HashMap
     */
    public static HashMap<String, Integer> sortHashMap(HashMap<String, Integer> unsortedMap, final boolean order) {
        List<HashMap.Entry<String, Integer>> list = new LinkedList<HashMap.Entry<String, Integer>>(unsortedMap.entrySet());
        // Sorting the list based on values
        Collections.sort(list, new Comparator<HashMap.Entry<String, Integer>>() {
            public int compare(HashMap.Entry<String, Integer> o1,
                               HashMap.Entry<String, Integer> o2) {
                if (order) {
                    return o1.getValue().compareTo(o2.getValue());
                }
                else {
                    return o2.getValue().compareTo(o1.getValue());
                }
            }
        });
        // Maintaining insertion order with the help of LinkedList
        HashMap<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (HashMap.Entry<String, Integer> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    /**
     * Method to make a grid map of the current map
     * @param noFlyZones ArrayList of Polygon object which is the no-fly zone
     * @return 2-D array which represents the grid on the map with -1 indicates
     * no-fly zone otherwise the value is 0
     */
    public static int[][] makeAMap (ArrayList<Polygon> noFlyZones) {
        int numberOfXGrid = (int) ((MAX_LONGITUDE - MIN_LONGITUDE) / 0.00015) + 1;
        int numberOfYGrid = (int) ((MAX_LATITUDE - MIN_LATITUDE) / 0.00015) + 1;
        int [][] map = new int[numberOfYGrid][numberOfXGrid];
        for (int j = 0; j < numberOfYGrid; j++) {
            double currentLatitude = MIN_LATITUDE + j * 0.00015;
            for (int i = 0; i < numberOfXGrid; i++) {
                double currentLongitude = MIN_LONGITUDE + i * 0.00015;
                LongLat currentLongLat = new LongLat(currentLongitude, currentLatitude);
                if (currentLongLat.inFlyZones(noFlyZones) || !currentLongLat.isConfined()) {
                    map[j][i] = -1;
                    if (j != 0) {
                        map[j-1][i] = -1;
                    }
                    if (j != numberOfYGrid - 1) {
                        map[j+1][i] = -1;
                    }
                    if (i != 0) {
                        map[j][i-1] = -1;
                    }
                    if (i != numberOfXGrid - 1) {
                        map[j][i+1] = -1;
                    }
                }
                else {
                    if (map[j][i] != -1) {
                        map[j][i] = 0;
                    }
                }
            }
        }
        return map;
    }

    /**
     * Method to get LongLat object from w3w formatted string
     * @param w3wString w3w formatted string
     * @param machineName Name of the machine the program runs
     * @param serverPort The server port
     * @return LongLat of the w3w string
     */
    public static LongLat getLongLatFromW3W(String w3wString, String machineName, String serverPort) {
        List<String> wordList = Arrays.asList(w3wString.split("\\."));
        String firstWord = wordList.get(0);
        String secondWord = wordList.get(1);
        String thirdWord = wordList.get(2);
        Words restaurantW3W = new Words(firstWord, secondWord, thirdWord, machineName, serverPort);
        return restaurantW3W.getLongLat();
    }


//    public static ArrayList<LongLat> getShopsLandmarks(List<LongLat> landmarks, ArrayList<LongLat> shops) {
//        ArrayList<LongLat> shopsLandmarks = new ArrayList<>();
//        for (LongLat shop : shops) {
//            double minDistance = shop.distanceTo(landmarks.get(0));
//            shopsLandmarks.add(landmarks.get(0));
//            for (int i = 1; i < landmarks.size(); i++) {
//                double currentDistance = shop.distanceTo(landmarks.get(i));
//                if (currentDistance < minDistance) {
//                    minDistance = currentDistance;
//                    shopsLandmarks.remove(shopsLandmarks.size() - 1);
//                    shopsLandmarks.add(landmarks.get(i));
//                }
//            }
//            shopsLandmarks.add(shop);
//        }
//        return shopsLandmarks;
//    }

    /**
     * Simple method to get LongLat from two doubles
     * @param x longitude
     * @param y latitude
     * @return LongLat object
     */
    public static LongLat getLongLatFromDoubles(double x, double y) {
        return new LongLat(x,y);
    }

    /**
     * Method to get List of all Node class which contain coordinates of move to next destination
     * @param drone Drone object
     * @param destination LongLat of next destination
     * @param map 2-d int array which represents the grid
     * @return List of all Node class
     */
    public static List<PathfindingAlgorithm.Node> getMoveNodes(Drone drone, LongLat destination, int[][] map) {
        // Get current drone longitude and latitude
        double longStart = drone.getDroneLongLat().getLongitude();
        double latStart = drone.getDroneLongLat().getLatitude();

        // Get current destination longitude and latitude
        double longEnd = destination.getLongitude();
        double latEnd = destination.getLatitude();

        // Initialise new algorithm object to be used to find paths to destination
        PathfindingAlgorithm algorithm = new PathfindingAlgorithm(map, longStart, latStart, false);
        List<PathfindingAlgorithm.Node> moveNodes = algorithm.findPathTo(longEnd, latEnd);
        return moveNodes;
    }

    /**
     * Method to initialise new FLightpath object
     * @param orderNo String order number
     * @param drone Drone object
     * @param nextLong double longitude of destination
     * @param nextLat double latitude of destination
     * @return Flightpath class
     */
    public static Flightpath getFlightpath(String orderNo, Drone drone, double nextLong, double nextLat) {
        LongLat nextPoint = HelperMethods.getLongLatFromDoubles(nextLong, nextLat);
        double fromLongitude = drone.getDroneLongLat().getLongitude();
        double fromLatitude = drone.getDroneLongLat().getLatitude();
        int currentAngle = drone.getDroneLongLat().getAngle(nextPoint);

        return new Flightpath(orderNo, fromLongitude, fromLatitude, currentAngle, nextLong, nextLat);
    }

    /**
     * Method to get Flightpath class if the drone hover
     * @param orderNo String order number
     * @param nextLong double longitude of destination
     * @param nextLat double latitude of destination
     * @return Flightpath class
     */
    public static Flightpath getFlightpathHover(String orderNo, double nextLong, double nextLat) {
        return new Flightpath(orderNo, nextLong, nextLat, -999, nextLong, nextLat);
    }

    /**
     * Method to calculate number of moves to Appleton Tower from current location
     * @param drone Drone drone object
     * @param appletonTower LongLat of destination which is Appleton Tower
     * @param map 2-d int array which represents the grid
     * @return int number of moves
     */
    public static int movesToAppletonTower(Drone drone, LongLat appletonTower, int[][] map) {
        double longStart = drone.getDroneLongLat().getLongitude();
        double latStart = drone.getDroneLongLat().getLatitude();
        double longEnd = appletonTower.getLongitude();
        double latEnd = appletonTower.getLatitude();

        PathfindingAlgorithm newAStar = new PathfindingAlgorithm(map, longStart,latStart, false);
        List<PathfindingAlgorithm.Node> travelNodes = newAStar.findPathTo(longEnd, latEnd);
        return travelNodes.size();
    }
}
