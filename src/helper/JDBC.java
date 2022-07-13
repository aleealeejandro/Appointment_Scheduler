package helper;

import java.sql.DriverManager;
import java.sql.Connection;

/**
 * Connects and disconnects to the database
 * @author Alexander Padilla
 */
public abstract class JDBC {
    private static final String protocol = "jdbc";
    private static final String vendor = ":mysql:";
    private static final String location = "//localhost/";
    private static final String databaseName = "client_schedule";
//    private static final String jdbcUrl = protocol + vendor + location + databaseName + "?connectionTimeZone = SERVER"; // LOCAL
    private static final String jdbcUrl = protocol + vendor + location + databaseName;
    private static final String driver = "com.mysql.cj.jdbc.Driver"; // Driver reference
    private static final String userName = "sqlUser"; // Username
    private static String password = "Passw0rd!"; // Password
    public static Connection connection;  // Connection Interface

    /**
     * opens a connection to this database
     */
    public static void openConnection() {
        // establishes connection
        try {
            Class.forName(driver); // Locate Driver
            connection = DriverManager.getConnection(jdbcUrl, userName, password); // Reference Connection object
            System.out.println("Connection successful!");
        } catch(Exception e) {
            System.out.println("Error:" + e.getMessage());
        }
    }

    /**
     * closes this connection to this database
     */
    public static void closeConnection() {
        try {
            connection.close();
            System.out.println("Connection closed!");
        } catch(Exception e) {
            System.out.println("Error:" + e.getMessage());
        }
    }

    /**
     * gets connection to this database
     *
     * @return connection
     */
    public static Connection getConnection(){
        return connection;
    }
}
