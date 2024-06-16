package com.project.fflb.data;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * This class secures a connection to the database, and handles calls to the database as well.
 *
 * @author Simon
 */
public abstract class DataHandler {
    /**
     * The singular connection held by the DBOData objects.
     */
    private static Connection connection;

    //Connection is established upon the object's instantiation
    public DataHandler() {
        openConnection();
    }

    /**
     * Establishes connection to a database, contained in the {@link #connection} instance variable.
     */
    protected static void openConnection() {
        if (!ConnectionHandler.isConnectionOpen()) {
            ConnectionHandler.setDatabaseName("FFLB_DB");
        }

        connection = ConnectionHandler.i();
    }

    /**
     * Calls a stored procedure from the database.
     *
     * @param procedure Name of the stored procedure.
     * @return CallableStatement from the procedure, which can be handled and/or executed.
     */
    public static CallableStatement makeCall(String procedure) {
        try  {
            return connection.prepareCall(procedure);
        } catch (SQLException e) {
            System.out.println("Could not create callable statement - " + e.getMessage());
            return null;
        }
    }

    /**
     * Close the database connection.
     * @return True if connection was open before closing
     */
    public static boolean close() {
        //Close connection if open
        if (ConnectionHandler.isConnectionOpen()) {
            return ConnectionHandler.closeConnection();
        }

        System.out.println("No connection to close.");
        return false;
    }

    /**
     * Whether the connection has been established.
     * @return 'True' if connection is open
     */
    public static boolean hasConnection() {
        return ConnectionHandler.isConnectionOpen();
    }
}
