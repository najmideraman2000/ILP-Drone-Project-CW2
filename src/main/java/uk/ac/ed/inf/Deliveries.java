package uk.ac.ed.inf;

public class Deliveries {
    public String orderNo;
    public String deliveredTo;
    public int costInPence;

    public Deliveries(String orderNo, String deliveredTo, int costInPence) {
        this.orderNo = orderNo;
        this.deliveredTo = deliveredTo;
        this.costInPence = costInPence;
    }
}
