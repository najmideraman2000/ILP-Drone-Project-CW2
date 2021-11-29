package uk.ac.ed.inf;

public class Drone {
    private int availableMoves;
    private LongLat droneLongLat;

    public Drone(Integer availableMoves, double longitudes, double latitudes) {
        this.availableMoves = availableMoves;
        this.droneLongLat = new LongLat(longitudes, latitudes);
    }

    public int getAvailableMoves() {
        return availableMoves;
    }
    public LongLat getDroneLongLat() {
        return droneLongLat;
    }
    public double getDroneLongitude() {
        return getDroneLongLat().getLongitude();
    }
    public double getDroneLatitude() {
        return getDroneLongLat().getLatitude();
    }

    public void setAvailableMoves(int newAvailableMoves) {
        this.availableMoves = newAvailableMoves;
    }
    public void setDroneLongLat(double newLongitude, double newLatitude) {
        this.droneLongLat.setLongitude(newLongitude);
        this.droneLongLat.setLatitude(newLatitude);
    }
}
