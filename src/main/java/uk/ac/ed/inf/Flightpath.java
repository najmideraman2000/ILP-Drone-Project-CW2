package uk.ac.ed.inf;

public class Flightpath {
    public String orderNo;
    public double fromLongitude;
    public double fromLatitude;
    public int angle;
    public double toLongitude;
    public double toLatitude;

    public Flightpath (String orderNo, double fromLongitude, double fromLatitude, int angle, double toLongitude, double toLatitude) {
        this.orderNo = orderNo;
        this.fromLongitude = fromLongitude;
        this.fromLatitude = fromLatitude;
        this.angle = angle;
        this.toLongitude = toLongitude;
        this.toLatitude = toLatitude;
    }
}
