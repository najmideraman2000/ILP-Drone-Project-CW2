package uk.ac.ed.inf;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class PathfindingAlgorithm {
    private final List<Node> open;
    private final List<Node> closed;
    private final List<Node> path;
    private final int[][] maze;
    private Node now;
    private final double xStart;
    private final double yStart;
    private double xEnd, yEnd;
    private final boolean diagonal;

    public static final double MIN_LONGITUDE = -3.192473;
    public static final double MAX_LONGITUDE = -3.184319;
    public static final double MIN_LATITUDE = 55.942617;
    public static final double MAX_LATITUDE = 55.946233;

    static class Node implements Comparable {
        public Node parent;
        public double x, y;
        public double g;
        public double h;
        Node(Node parent, double xPos, double yPos, double g, double h) {
            this.parent = parent;
            this.x = xPos;
            this.y = yPos;
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

    public PathfindingAlgorithm(int[][] maze, double xStart, double yStart, boolean diagonal) {
        this.open = new ArrayList<>();
        this.closed = new ArrayList<>();
        this.path = new ArrayList<>();
        this.maze = maze;
        this.now = new Node(null, xStart, yStart, 0, 0);
        this.xStart = xStart;
        this.yStart = yStart;
        this.diagonal = diagonal;
    }

    public List<Node> findPathTo(double xEnd, double yEnd) {
        this.xEnd = xEnd;
        this.yEnd = yEnd;
        this.closed.add(this.now);
        addNeighboursToOpenList();
        while (!(HelperMethods.getLongLatFromDoubles(this.now.x, this.now.y).closeTo(HelperMethods.getLongLatFromDoubles(this.xEnd, this.yEnd)))) {
            if (this.open.isEmpty()) { // Nothing to examine
                return null;
            }
            this.now = this.open.get(0); // get first node (lowest f score)
            this.open.remove(0); // remove it
            this.closed.add(this.now); // and add to the closed
            addNeighboursToOpenList();
        }
        this.path.add(0, this.now);
        while (!(HelperMethods.getLongLatFromDoubles(this.now.x, this.now.y).closeTo(HelperMethods.getLongLatFromDoubles(this.xStart, this.yStart)))) {
            this.now = this.now.parent;
            this.path.add(0, this.now);
        }
        return this.path;
    }

    private static boolean findNeighborInList(List<Node> array, Node node) {
        return array.stream().anyMatch((n) -> (n.x == node.x && n.y == node.y));
    }

    private double distance(double dx, double dy) {
        if (this.diagonal) { // if diagonal movement is alloweed
            return Math.hypot(this.now.x + dx - this.xEnd, this.now.y + dy - this.yEnd); // return hypothenuse
        } else {
            return Math.abs(this.now.x + dx - this.xEnd) + Math.abs(this.now.y + dy - this.yEnd); // else return "Manhattan distance"
        }
    }

    private void addNeighboursToOpenList() {
        Node node;
        for (double x = -0.00015; x <= 0.00015; x+=0.00015) {
            for (double y = -0.00015; y <= 0.00015; y+=0.00015) {
                if (!this.diagonal && x != 0 && y != 0) {
                    continue; // skip if diagonal movement is not allowed
                }
                node = new Node(this.now, this.now.x + x, this.now.y + y, this.now.g, this.distance(x, y));
                if ((x != 0 || y != 0) // not this.now
                        && this.now.x + x >= MIN_LONGITUDE && this.now.x + x < MAX_LONGITUDE // check maze boundaries
                        && this.now.y + y >= MIN_LATITUDE && this.now.y + y < MAX_LATITUDE
                        && this.maze[(int) ((this.now.y + y - MIN_LATITUDE)/0.00015)][(int) ((this.now.x + x - MIN_LONGITUDE)/0.00015)] != -1
//                        && this.maze[(int) ((this.now.y + y - MIN_LATITUDE)/0.00015)][(int) ((this.now.x + x - MIN_LONGITUDE)/0.00015)] != 100// check if square is walkable
                        && !findNeighborInList(this.open, node) && !findNeighborInList(this.closed, node)) { // if not already done
                    node.g = node.parent.g + 1.; // Horizontal/vertical cost = 1.0
                    node.g += maze[(int) ((this.now.y + y - MIN_LATITUDE)/0.00015)][(int) ((this.now.x + x - MIN_LONGITUDE)/0.00015)]; // add movement cost for this square

                    // diagonal cost = sqrt(hor_cost² + vert_cost²)
                    // in this example the cost would be 12.2 instead of 11
                        /*
                        if (diag && x != 0 && y != 0) {
                            node.g += .4;	// Diagonal movement cost = 1.4
                        }
                        */
                    this.open.add(node);
                }
            }
        }
        Collections.sort(this.open);
    }
}
