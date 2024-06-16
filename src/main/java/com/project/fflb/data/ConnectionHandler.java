package com.project.fflb.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton class with a {@link Connection} object that is instantiated once and only closed upon request.
 *
 * @author Ebbe
 */
public class ConnectionHandler {
    //Singleton instance
    private static Connection connection;

    //Private constructor to prevent instantiation
    private ConnectionHandler() {}

    /**
     * Name of the database to connect to
     */
    private static String databaseName;

    /**
     * Establish the connection.
     *
     * @return Result of operation
     */
    public static boolean openConnection() {
        //Cannot connect if no database name has been set
        if (databaseName == null) {
            System.out.println("Could not establish connection: No database name specified.");
            return false;
        }

        try {
            /**
             * The connection string for the database.
             * It uses integratedSecurity (Also known as Windows Authentication) This means that we don't need a username and password
             * It just uses the users windows login credentials instead
             *
             * TrustServerCertificate, makes it easier to use the SQL database for school use, since no valid SSL certificate is needed to open a connection
             *
             * Source: https://www.kaspersky.com/resource-center/definitions/what-is-a-ssl-certificate
             * SSL: Secure Sockets layer
             * It's a security protocol that creates and encrypted link between a web server and browser
             */
            String connectionString =
                    "jdbc:sqlserver://localhost:1433;" +
                            "instanceName=SQLEXPRESS;" +
                            "databaseName=" + databaseName + ";" +
                            "integratedSecurity=true;" +
                            "trustServerCertificate=true;";

            System.out.println("Connecting to database "+databaseName+"...");

            //Establish connection
            connection = DriverManager.getConnection(connectionString);

            System.out.println("Connected to database "+databaseName);

            return true;
        } catch (SQLException e) {
            System.out.println("Could not connect to database " + databaseName + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Grab connection instance and initialize it if needed.
     *
     * @return Singleton instance
     */
    public static Connection i() {
        //Instantiate if null
        if (connection == null) {
            openConnection();
        }

        return connection;
    }

    /**
     * @return boolean of whether connection is open
     */
    public static boolean isConnectionOpen() {
        if (connection == null) {
            return false;
        }

        try {
            if (connection.isClosed()) {
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Error checking for the closed connection: "+e.getMessage());
        }

        return true;
    }

    /**
     * Close connection and set connection to null.
     *
     * @return Result of operation
     */
    public static boolean closeConnection() {
        try {
            connection.close();
            connection = null;
            return true;
        } catch (SQLException e) {
            System.out.println("Unable to close connection to "+databaseName + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Get name of the current database.
     *
     * @return String
     */
    public static String getDatabaseName() {
        return databaseName;
    }

    /**
     * Set the name of the database to connect to.
     *
     * @param databaseName Database name
     */
    public static void setDatabaseName(String databaseName) {
        ConnectionHandler.databaseName = databaseName;
    }
}
