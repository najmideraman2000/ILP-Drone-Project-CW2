package uk.ac.ed.inf;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class PathfindingAlgorithm {
    private final List<Node> openList;
    private final List<Node> closedList;
    private final List<Node> pathList;
    private final int[][] map;
    private Node currentNode;
    private final double longStart;
    private final double latStart;
    private double longEnd, latEnd;

    public static final double MIN_LONGITUDE = -3.192473;
    public static final double MAX_LONGITUDE = -3.184319;
    public static final double MIN_LATITUDE = 55.942617;
    public static final double MAX_LATITUDE = 55.946233;

    static class Node implements Comparable {
        public Node parentNode;
        public double longitude, latitude;
        public double g;
        public double h;
        Node(Node parent, double longitude, double latitude, double g, double h) {
            this.parentNode = parent;
            this.longitude = longitude;
            this.latitude = latitude;
            this.g = g;
            this.h = h;
        }
        // Compare by f value (g + h)
        @Override
        public int compareTo(Object o) {
            Node that = (Node) o;
            return (int)((this.g + this.h) - (that.g + that.h));
        }
    }

    public PathfindingAlgorithm(int[][] map, double longStart, double latStart) {
        this.openList = new ArrayList<>();
        this.closedList = new ArrayList<>();
        this.pathList = new ArrayList<>();
        this.map = map;
        this.currentNode = new Node(null, longStart, latStart, 0, 0);
        this.longStart = longStart;
        this.latStart = latStart;
    }

    public List<Node> findPathTo(double longEnd, double latEnd) {
        this.longEnd = longEnd;
        this.latEnd = latEnd;
        this.closedList.add(this.currentNode);
        addNeighboursToOpenList();
        while (!(HelperMethods.getLongLatFromDoubles(this.currentNode.longitude, this.currentNode.latitude).closeTo(HelperMethods.getLongLatFromDoubles(this.longEnd, this.latEnd)))) {
            if (this.openList.isEmpty()) { // Nothing to examine
                return null;
            }
            this.currentNode = this.openList.get(0); // get first node (lowest f score)
            this.openList.remove(0); // remove it
            this.closedList.add(this.currentNode); // and add to the closed
            addNeighboursToOpenList();
        }
        this.pathList.add(0, this.currentNode);
        while (!(HelperMethods.getLongLatFromDoubles(this.currentNode.longitude, this.currentNode.latitude).closeTo(HelperMethods.getLongLatFromDoubles(this.longStart, this.latStart)))) {
            this.currentNode = this.currentNode.parentNode;
            this.pathList.add(0, this.currentNode);
        }
        return this.pathList;
    }

    private static boolean findNeighborInList(List<Node> nodeList, Node node) {
        return nodeList.stream().anyMatch((n) -> (n.longitude == node.longitude && n.latitude == node.latitude));
    }

    private double distance(double oneMoveLong, double oneMoveLat) {
        return Math.abs(this.currentNode.longitude + oneMoveLong - this.longEnd) + Math.abs(this.currentNode.latitude + oneMoveLat - this.latEnd);
    }

    private void addNeighboursToOpenList() {
        Node node;
        for (double moveLong = -0.00015; moveLong <= 0.00015; moveLong += 0.00015) {
            for (double moveLat = -0.00015; moveLat <= 0.00015; moveLat += 0.00015) {
                if (moveLong != 0 && moveLat != 0) {
                    continue; // skip if diagonal movement is not allowed
                }
                node = new Node(this.currentNode, this.currentNode.longitude + moveLong, this.currentNode.latitude + moveLat, this.currentNode.g, this.distance(moveLong, moveLat));
                if ((moveLong != 0 || moveLat != 0) // not this.now
                        && this.currentNode.longitude + moveLong >= MIN_LONGITUDE && this.currentNode.longitude + moveLong < MAX_LONGITUDE // check maze boundaries
                        && this.currentNode.latitude + moveLat >= MIN_LATITUDE && this.currentNode.latitude + moveLat < MAX_LATITUDE
                        && this.map[(int) ((this.currentNode.latitude + moveLat - MIN_LATITUDE)/0.00015)][(int) ((this.currentNode.longitude + moveLong - MIN_LONGITUDE)/0.00015)] != -1
                        && !findNeighborInList(this.openList, node) && !findNeighborInList(this.closedList, node)) { // if not already done
                    node.g = node.parentNode.g + 1.; // Horizontal/vertical cost = 1.0
                    node.g += map[(int) ((this.currentNode.latitude + moveLat - MIN_LATITUDE)/0.00015)][(int) ((this.currentNode.longitude + moveLong - MIN_LONGITUDE)/0.00015)]; // add movement cost for this square
                    this.openList.add(node);
                }
            }
        }
        Collections.sort(this.openList);
    }
}
