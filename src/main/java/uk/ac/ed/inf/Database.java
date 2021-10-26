package uk.ac.ed.inf;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.sql.*;
import java.util.ArrayList;

public class Database {
    private String machineName = new String("localhost");
    private String databasePort = new String("80");

    public Database(String machineName, String databasePort) {
        this.machineName = machineName;
        this.databasePort = databasePort;
    }

    // Initialise new HttpClient and request menus.json from the web server
    String jdbcString = "jdbc:derby://" + machineName + ":" + databasePort + "/derbyDB";

    public void fromDatabase() {
        try {
            Connection conn = DriverManager.getConnection(jdbcString);
            Statement statement = conn.createStatement();

            final String allQuery = "select * from orders where deliveryDate=(2022-04-11)";
            PreparedStatement psAllQuery = conn.prepareStatement(allQuery);

            ArrayList<String> orderNoList = new ArrayList<>();
            ArrayList<String> customerList = new ArrayList<>();
            ArrayList<String> deliverToList = new ArrayList<>();

            ResultSet rs = psAllQuery.executeQuery();

            while (rs.next()) {
                String orderNo = rs.getString("orderNo");
                String customer = rs.getString("customer");
                String deliverTo = rs.getString("deliverTo");

                orderNoList.add(orderNo);
                customerList.add(customer);
                deliverToList.add(deliverTo);
            }


        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
