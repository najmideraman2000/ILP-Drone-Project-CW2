package uk.ac.ed.inf;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private final String machineName;
    private final String day;
    private final String month;
    private final String year;
    private final String databasePort;

    public Database(String machineName, String day, String month, String year, String databasePort) {
        this.machineName = machineName;
        this.day = day;
        this.month = month;
        this.year = year;
        this.databasePort = databasePort;
    }

    /**
     * Method to get info from database consist of order number, delivery places, and item
     * @return An ArrayList consist of three ArrayList from order number, delivery place, and item respectively
     */
    public ArrayList<ArrayList<String>> getDatabaseInfo() {
        String jdbcString = "jdbc:derby://" + machineName + ":" + databasePort + "/derbyDB";
        ArrayList<ArrayList<String>> resultList = new ArrayList<>();

        try {
            Connection conn = DriverManager.getConnection(jdbcString);
            final String allQuery = "select * from orders A JOIN orderDetails B on A.orderNo = B.orderNo where deliveryDate=(?)";
            PreparedStatement psAllQuery = conn.prepareStatement(allQuery);
            psAllQuery.setString(1, year + "-" + month + "-" + day);
            ResultSet rs = psAllQuery.executeQuery();

            ArrayList<String> orderNoList = new ArrayList<>();
            ArrayList<String> deliverToList = new ArrayList<>();
            ArrayList<String> itemList = new ArrayList<>();

            while (rs.next()) {
                String orderNo = rs.getString("orderNo");
                String deliverTo = rs.getString("deliverTo");
                String item = rs.getString("item");

                orderNoList.add(orderNo);
                deliverToList.add(deliverTo);
                itemList.add(item);
            }

            resultList.add(orderNoList);
            resultList.add(deliverToList);
            resultList.add(itemList);

        } catch(Exception e) {
            e.printStackTrace();
        }
        return resultList;
    }

    /**
     * Method to create a 'flightpath' table in database
     * consist of orderNo, fromLongitude, fromLatitude, angle, toLongitude, toLatitude
     * @param flightPaths List of Flightpath class which is flightpath made by the drone during the program
     */
    public void createFlightpathTable(List<Flightpath> flightPaths) {
        String jdbcString = "jdbc:derby://" + machineName + ":" + databasePort + "/derbyDB";

        try {
            Connection conn = DriverManager.getConnection(jdbcString);
            Statement statement = conn.createStatement();

            DatabaseMetaData databaseMetaData = conn.getMetaData();
            ResultSet resultSet = databaseMetaData.getTables(null, null, "FLIGHTPATH", null);
            if(resultSet.next()) {
                statement.execute("drop table flightpath");
            }
            statement.execute("create table flightpath(orderNo char(8), fromLongitude double, fromLatitude double, angle integer, toLongitude double, toLatitude double)");
            PreparedStatement psFlightpath = conn.prepareStatement("insert into flightpath values (?, ?, ?, ?, ?, ?)");

            for (Flightpath flightpath : flightPaths) {
                psFlightpath.setString(1, flightpath.orderNo);
                psFlightpath.setDouble(2, flightpath.fromLongitude);
                psFlightpath.setDouble(3, flightpath.fromLatitude);
                psFlightpath.setInt(4, flightpath.angle);
                psFlightpath.setDouble(5, flightpath.toLongitude);
                psFlightpath.setDouble(6, flightpath.toLatitude);
                psFlightpath.execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to create a 'deliveries' table in database
     * consist of order number, delivery place, delivery cost (in pence)
     * @param deliveries List of Deliveries class which is deliveries made by the drone during the program
     */
    public void createDeliveriesTable(List<Deliveries> deliveries) {
        String jdbcString = "jdbc:derby://" + machineName + ":" + databasePort + "/derbyDB";

        try {
            Connection conn = DriverManager.getConnection(jdbcString);
            Statement statement = conn.createStatement();

            DatabaseMetaData databaseMetaData = conn.getMetaData();
            ResultSet resultSet = databaseMetaData.getTables(null, null, "DELIVERIES", null);
            if(resultSet.next()) {
                statement.execute("drop table deliveries");
            }
            statement.execute("create table deliveries(orderNo char(8), deliveredTo varchar(19), costInPence int)");
            PreparedStatement psDeliveries = conn.prepareStatement("insert into deliveries values (?, ?, ?)");

            for (Deliveries delivery : deliveries) {
                psDeliveries.setString(1, delivery.orderNo);
                psDeliveries.setString(2, delivery.deliveredTo);
                psDeliveries.setInt(3, delivery.costInPence);
                psDeliveries.execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
