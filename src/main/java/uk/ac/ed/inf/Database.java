package uk.ac.ed.inf;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private String machineName;
    private String databasePort;

    public Database(String machineName, String databasePort) {
        this.machineName = machineName;
        this.databasePort = databasePort;
    }

    public List[] allList(String day, String month, String year) {
        String jdbcString = "jdbc:derby://" + machineName + ":" + databasePort + "/derbyDB";
        try {
            Connection conn = DriverManager.getConnection(jdbcString);

            final String allQuery = "select * from orders where deliveryDate=(?)";
            PreparedStatement psAllQuery = conn.prepareStatement(allQuery);
            psAllQuery.setString(1, year + "-" + month + "-" + day);

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
            return new List[] {orderNoList, customerList};

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
