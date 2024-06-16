package com.project.fflb.data.dboData;

import com.project.fflb.data.DataHandler;
import com.project.fflb.dbo.Salesman;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Handles all database/data handling for the Salesman class.
 *
 * @author Simon
 */
public class SalesmanData extends DataHandler {
    /**
     * Insert a new salesman into the salesman table.
     * @param salesman Inserted {@link Salesman} object
     */
    public static void create(Salesman salesman) {
        //Cannot proceed if connection isn't established
        if (!hasConnection()) {
            openConnection();
        }

        //Make a call to the database using the super class method "makeCall" Which calls a stored procedure
        try (CallableStatement cs = makeCall("{CALL salesman_addToDB(?, ?, ?, ?, ?, ?)}")) {
            cs.setString(1, salesman.getFirstName());
            cs.setString(2, salesman.getLastName());
            cs.setString(3, salesman.getEmail());
            cs.setInt(4, salesman.getPhoneNumber());
            cs.setDouble(5, salesman.getSalesmanLoanLimit());
            cs.setInt(6, salesman.getIsActive() ? 1 : 0); //Convert bool to int

            cs.execute();
        } catch (SQLException e) {
            System.out.println("Could not add salesman to the database - " + e.getMessage());
        }
    }

    /**
     * Fetch a salesman object from the salesman table.
     * @param id ID of the salesman to fetch
     * @return {@link Salesman} object
     */
    public static Salesman get(int id) {
        //Cannot proceed if connection isn't established
        if (!hasConnection()) {
            openConnection();
        }

        Salesman salesman = null;
        //Make a call to the database using the super class method "makeCall" Which calls a stored procedure
        try (CallableStatement cs = makeCall("{CALL salesman_getFromDB_ByID(?)}")) {
            cs.setInt(1, id);
            ResultSet resultset = cs.executeQuery();

            //Get first element from call result
            try {
                salesman = createFromResultSet(resultset).get(0);
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("No salesmen found!");
            }
        } catch (SQLException e) {
            System.out.println("Could not get salesman from database - " + e.getMessage());
        }
        return salesman;
    }

    /**
     * Fetch all salesmen from the salesman table.
     * @return ArrayList of {@link Salesman} objects
     */
    public static ArrayList<Salesman> getAll() {
        //Cannot proceed if connection isn't established
        if (!hasConnection()) {
            openConnection();
        }

        ArrayList<Salesman> list = null;

        //Make a call to the database using the super class method "makeCall" Which calls a stored procedure
        try(CallableStatement cs = makeCall("{CALL salesman_getAll}")) {
            ResultSet resultset = cs.executeQuery();

            list = createFromResultSet(resultset);
        } catch (SQLException e) {
            System.out.println("Could not get all salesmen from database - " + e.getMessage());
        }

        return list;
    }

    /**
     * Update a salesman in the salesman table by overwriting its data with a new salesman.
     * @param salesman {@link Salesman} object with new data
     */
    public static void update(Salesman salesman) {
        //Cannot proceed if connection isn't established
        if (!hasConnection()) {
            openConnection();
        }

        //Make a call to the database using the super class method "makeCall" Which calls a stored procedure
        try (CallableStatement cs = makeCall("{CALL salesman_updateSalesman_ByID(?, ?, ?, ?, ?, ?, ?)}")) {
            cs.setInt(1, salesman.getPersonID());
            cs.setString(2, salesman.getFirstName());
            cs.setString(3, salesman.getLastName());
            cs.setString(4, salesman.getEmail());
            cs.setInt(5, salesman.getPhoneNumber());
            cs.setDouble(6, salesman.getSalesmanLoanLimit());
            cs.setInt(7, salesman.getIsActive() ? 1 : 0);

            cs.execute();
        } catch (SQLException e) {
            System.out.println("Could not update salesman in database - " + e.getMessage());
        }
    }

    /**
     * Delete a salesman from the salesman table.
     * @param id ID of the salesman to delete
     */
    public static void delete(int id) {
        //Cannot proceed if connection isn't established
        if (!hasConnection()) {
            openConnection();
        }
        //Make a call to the database using the super class method "makeCall" Which calls a stored procedure
        try (CallableStatement cs = makeCall("{CALL salesman_deleteSalesman_ByID(?)}")) {
            cs.setInt(1, id);

            cs.execute();
        } catch (SQLException e) {
            System.out.println("Could not delete salesman from database - " + e.getMessage());
        }
    }

    /**
     * Construct list of {@link Salesman} objects from a result set.
     *
     * @param resultset ResultSet from SQL procedure call.
     * @return ArrayList of Customer objects.
     * @throws SQLException
     */
    protected static ArrayList<Salesman> createFromResultSet(ResultSet resultset) throws SQLException {

        ArrayList<Salesman> list = new ArrayList<>();

        //Loop through all results from the set and constructs DBO's from it
        while (resultset.next()) {
            int salesman_id = resultset.getInt("salesman_id");
            String firstName = resultset.getString("first_name");
            String lastName = resultset.getString("last_name");
            String email = resultset.getString("email");
            int phoneNumber = resultset.getInt("phone_number");
            double loanLimit = resultset.getDouble("loan_limit");
            boolean isActive = resultset.getInt("is_active") != 0; //Convert int to bool

            list.add(new Salesman(salesman_id, firstName, lastName, email, phoneNumber, loanLimit, isActive));
        }

        return list;
    }
}
