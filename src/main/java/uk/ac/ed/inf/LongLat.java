package uk.ac.ed.inf;

public class LongLat {

    public double longitude;
    public double latitude;
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
        double secondLongitude = longLatObject.longitude;
        double secondLatitude = longLatObject.latitude;
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
    public LongLat nextPosition(int angle) {
        double newLongitude;
        double newLatitude;
        // In the case where the drone is hovering, junk value of -999 is given.
        if (angle == -999) {
            newLongitude = longitude;
            newLatitude = latitude;
        }
        else {
            newLongitude = longitude + (0.00015 * (Math.cos(Math.toRadians(angle))));
            newLatitude = latitude + (0.00015 * (Math.sin(Math.toRadians(angle))));
        }
        return new LongLat(newLongitude, newLatitude);
    }
}
