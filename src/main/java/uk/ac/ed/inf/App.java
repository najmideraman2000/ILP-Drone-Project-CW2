package uk.ac.ed.inf;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        Menus menus = new Menus("localhost", "9898");
        int totalCost = menus.getDeliveryCost(
                "Ham and mozzarella Italian roll"
        );
    }
}
