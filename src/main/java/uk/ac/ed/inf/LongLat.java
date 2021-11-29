package uk.ac.ed.inf;

import com.mapbox.geojson.*;

import java.util.ArrayList;
import java.util.List;

import com.mapbox.turf.TurfJoins;

public class LongLat {
    private double longitude;
    private double latitude;
    public static final double MIN_LONGITUDE = -3.192473;
    public static final double MAX_LONGITUDE = -3.184319;
    public static final double MIN_LATITUDE = 55.942617;
    public static final double MAX_LATITUDE = 55.946233;

    /**
     * Constructor for LongLat class.
     *
     * @param longitude longitude of theLongLat object.
     * @param latitude latitude of the LongLat object.
     */
    public LongLat(double longitude, double latitude){
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }
    public double getLatitude() {
        return latitude;
    }

    public void setLongitude(double newLongitude) {
        this.longitude = newLongitude;
    }
    public void  setLatitude(double newLatitude) {
        this.latitude = newLatitude;
    }

    /**
     * Method to check whether the drone is inside the confinement area.
     *
     * @return boolean True if the drone is in the area. False otherwise.
     */
    public boolean isConfined() {
        return !(longitude <= MIN_LONGITUDE) && !(longitude >= MAX_LONGITUDE)
                && !(latitude <= MIN_LATITUDE) && !(latitude >= MAX_LATITUDE);
    }

    /**
     * Method to calculate the distance between the current position of the drone and another specified position.
     *
     * @param longLatObject the second LongLat object which contain the another position.
     * @return the distance between both position in degree.
     */
    public double distanceTo(LongLat longLatObject) {
        double secondLongitude = longLatObject.getLongitude();
        double secondLatitude = longLatObject.getLatitude();
        // Calculate the distance using Pythagoras Theorem formula
        return Math.sqrt(Math.pow((longitude - secondLongitude), 2) + Math.pow((latitude - secondLatitude), 2));
    }

    /**
     * Method to check whether the current position of the drone is close to another specified position.
     *
     * @param longLatObject the second LongLat object which contain the another position.
     * @return boolean True if the distance between both positions is close (less than 0.00015 degree). False otherwise.
     */
    public boolean closeTo(LongLat longLatObject) {
        double distance = distanceTo(longLatObject);
        return distance < 0.00015;
    }

    /**
     * Method to calculate the next position of the drone as the drone make a single move
     * to a direction of any angle (multiplies of 10).
     *
     * @param angle the angle at which the drone move.
     * @return new LongLat object which contains the new position of the drone.
     */
//    public LongLat nextPosition(int angle) {
//        double newLongitude;
//        double newLatitude;
//        // In the case where the drone is hovering, junk value of -999 is given.
//        if (angle == -999) {
//            newLongitude = longitude;
//            newLatitude = latitude;
//        }
//        else {
//            newLongitude = longitude + (0.00015 * (Math.cos(Math.toRadians(angle))));
//            newLatitude = latitude + (0.00015 * (Math.sin(Math.toRadians(angle))));
//        }
//        return new LongLat(newLongitude, newLatitude);
//    }

    /**
     * Method to calculate the angle between two LongLat objects
     * @param nextPosition next LongLat
     * @return the angle between the two LongLat
     */
    public int getAngle(LongLat nextPosition) {
        double x1 = longitude;
        double y1 = latitude;
        double x2 = nextPosition.getLongitude();
        double y2 = nextPosition.getLatitude();

        if (x2 > x1 && y2 > y1) {
            int angle = (int) Math.round((Math.toDegrees(Math.atan((y2 - y1) / (x2 - x1)))) / 10.0) * 10;
            return angle;
        }
        else if (x2 < x1 && y2 > y1) {
            int angle = (int) Math.round((Math.toDegrees(Math.atan((y2 - y1) / (x1 - x2)))) / 10.0) * 10;
            return 180 - angle;
        }
        else if (x2 < x1 && y2 < y1) {
            int angle = (int) Math.round((Math.toDegrees(Math.atan((y1 - y2) / (x1 - x2)))) / 10.0) * 10;
            return 180 + angle;
        }
        else if (x2 > x1 && y2 < y1) {
            int angle = (int) Math.round((Math.toDegrees(Math.atan((y1 - y2) / (x2 - x1)))) / 10.0) * 10;
            return 360 - angle;
        }
        else if (x2 > x1 && y2 == y1) {
            return 0;
        }
        else if (x2 == x1 && y2 > y1) {
            return 90;
        }
        else if (x2 < x1 && y2 == y1) {
            return 180;
        }
        else if (x2 == x1 && y2 < y1) {
            return 270;
        }
        return 0;
    }

    /**
     * Method to check whether the LongLat is in the no-fly zone or not
     * @param noFlyZones ArrayList of all Polygon objects of no-fly zones
     * @return boolean true if the LongLat is in the zone. Return false otherwise
     */
    public boolean inFlyZones(ArrayList<Polygon> noFlyZones) {
        Point coordinate = Point.fromLngLat(longitude, latitude);
        for(Polygon noFlyZone : noFlyZones) {
            if (TurfJoins.inside(coordinate, noFlyZone)) {
                return true;
            }
        }
        return false;
    }

//    public boolean closeToTwo(LongLat longLatObject) {
//        double distance = distanceTo(longLatObject);
//        return distance <= 0.00015;
//    }

//    public boolean closeToNoFlyZOnes(ArrayList<Polygon> noFlyZones) {
//        for (Polygon noFlyZone : noFlyZones) {
//            LineString outerLine = noFlyZone.outer();
//            List<Point> lineCoordinate = outerLine.coordinates();
//            for (Point lu : lineCoordinate) {
//                LongLat noFLyLongLat = new LongLat(lu.longitude(), lu.latitude());
//                if (this.closeToTwo(noFLyLongLat)) {
//                    return true;
//                }
//            }
//        }
//        return false;
//    }
}
