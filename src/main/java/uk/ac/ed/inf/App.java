package uk.ac.ed.inf;

import java.util.List;

public class App {

    public static void main(String[] args) {
        String day = args[0];
        String month = args[1];
        String year = args[2];
        String serverPort = args[3];
        String databasePort = args[4];

        Database newDatabase = new Database("localhost", databasePort);
        List infoList = new List[];

    }
}
