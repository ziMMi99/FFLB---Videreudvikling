package com.project.fflb.data.dboData;

import api.com.ferrari.finances.dk.rki.Rating;
import com.project.fflb.data.DataHandler;
import com.project.fflb.dbo.Customer;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Handles all database/data handling for the Customer class.
 *
 * @author Ebbe
 */
public class CustomerData extends DataHandler {
    /**
     * Insert a new customer into the customer table.
     * @param customer Inserted {@link Customer} object
     */
    public static void create(Customer customer) {
        //Cannot proceed if connection isn't established
        if (!hasConnection()) {
            openConnection();
        }
        //Make a call to the database using the super class method "makeCall" Which calls a stored procedure
        try (CallableStatement cs = makeCall("{CALL customer_addToDB(?, ?, ?, ?, ?, ?, ?, ?)}")) {
            cs.setString(1, customer.getFirstName());
            cs.setString(2, customer.getLastName());
            cs.setString(3, customer.getAddress());
            cs.setInt(4, customer.getPostCode());
            cs.setString(5, customer.getCPR());
            cs.setString(6, customer.getEmail());
            cs.setInt(7, customer.getPhoneNumber());
            cs.setString(8, customer.getCreditScore().toString());

            cs.execute();
        } catch (SQLException e) {
            System.out.println("Could not add customer to database - " + e.getMessage());
        }
    }

    /**
     * Fetch a customer object from the customer table.
     * @param id ID of the customer to fetch
     * @return {@link Customer} object
     */
    public static Customer get(int id) {
        //Cannot proceed if connection isn't established
        if (!hasConnection()) {
            openConnection();
        }

        Customer customer = null;
        //Make a call to the database using the super class method "makeCall" Which calls a stored procedure
        try(CallableStatement cs = makeCall("{CALL customer_getFromDB_ByID(?)}")) {
            cs.setInt(1, id);
            ResultSet resultset = cs.executeQuery();

            //Get first element from call result
            try {
                customer = createFromResultSet(resultset).get(0);
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("No customers found!");
            }
        } catch (SQLException e) {
            System.out.println("Could not get customer from database - " + e.getMessage());
        }

        return customer;
    }

    /**
     * Fetch all customers from the customer table.
     * @return ArrayList of {@link Customer} objects
     */
    public static ArrayList<Customer> getAll() {
        //Cannot proceed if connection isn't established
        if (!hasConnection()) {
            openConnection();
        }

        ArrayList<Customer> list = null;
        //Make a call to the database using the super class method "makeCall" Which calls a stored procedure
        try(CallableStatement cs = makeCall("{CALL customer_getAll}")) {
            ResultSet resultset = cs.executeQuery();

            list = createFromResultSet(resultset);

        } catch (SQLException e) {
            System.out.println("Could not get all customers from database - " + e.getMessage());
        }

        return list;
    }

    /**
     * Update a customer in the customer table by overwriting its data with a new customer.
     * @param customer {@link Customer} object with new data
     */
    public static void update(Customer customer) {
        //Cannot proceed if connection isn't established
        if (!hasConnection()) {
            openConnection();
        }
        //Make a call to the database using the super class method "makeCall" Which calls a stored procedure
        try (CallableStatement cs = makeCall("{CALL customer_updateCustomer_ByID(?, ?, ?, ?, ?, ?, ?, ?, ?)}")) {
            cs.setInt(1, customer.getPersonID());
            cs.setString(2, customer.getFirstName());
            cs.setString(3, customer.getLastName());
            cs.setString(4, customer.getAddress());
            cs.setInt(5, customer.getPostCode());
            cs.setString(6, customer.getCPR());
            cs.setString(7, customer.getEmail());
            cs.setInt(8, customer.getPhoneNumber());
            cs.setString(9, customer.getCreditScore().toString());

            cs.execute();
        } catch (SQLException e) {
            System.out.println("Could not update customer in the database - " + e.getMessage());
        }
    }

    /**
     * Delete a customer from the customer table.
     * @param id ID of the customer to delete
     */
    public static void delete(int id) {
        //Cannot proceed if connection isn't established
        if (!hasConnection()) {
            openConnection();
        }

        try (CallableStatement cs = makeCall("{CALL customer_deleteCustomer_ByID(?)}")) {
            cs.setInt(1, id);

            cs.execute();
        } catch (SQLException e) {
            System.out.println("Could not delete customer from database - " + e.getMessage());
        }
    }

    /**
     * Construct list of {@link Customer} objects from a result set.
     *
     * @param resultset ResultSet from SQL procedure call.
     * @return ArrayList of Customer objects.
     * @throws SQLException
     */
    protected static ArrayList<Customer> createFromResultSet(ResultSet resultset) throws SQLException {
        ArrayList<Customer> list = new ArrayList<>();

        //Loop through all results from the set and constructs DBO's from it
        while (resultset.next()) {
            int customer_id = resultset.getInt("customer_id");
            String first_name = resultset.getString("first_name");
            String last_name = resultset.getString("last_name");
            String home_address = resultset.getString("home_address");
            int post_code = resultset.getInt("post_code");
            String cpr = resultset.getString("cpr");
            String email = resultset.getString("email");
            int phone_number = resultset.getInt("phone_number");
            String str_credit_score = resultset.getString("credit_score");
            Rating credit_score = Rating.valueOf(str_credit_score);

            list.add(new Customer(
                    customer_id,
                    first_name,
                    last_name,
                    email,
                    phone_number,
                    home_address,
                    post_code,
                    cpr,
                    credit_score
            ));
        }

        return list;
    }
}
