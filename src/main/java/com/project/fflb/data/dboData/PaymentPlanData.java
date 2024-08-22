package com.project.fflb.data.dboData;

import com.project.fflb.data.DataHandler;
import com.project.fflb.dbo.Car;
import com.project.fflb.dbo.Customer;
import com.project.fflb.dbo.PaymentPlan;
import com.project.fflb.dbo.Salesman;

import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Handles all database/data handling for the PaymentPlan class.
 *
 * @author Ebbe
 */
public class PaymentPlanData extends DataHandler {
    /**
     * Insert a new payment plan into the paymentplan table.
     * @param paymentPlan Inserted {@link PaymentPlan} object
     */
    public static void create(PaymentPlan paymentPlan) {
        //Cannot proceed if connection isn't established
        if (!hasConnection()) {
            openConnection();
        }
        //Make a call to the database using the super class method "makeCall" Which calls a stored procedure
        try (CallableStatement cs = makeCall("{CALL paymentplan_addToDB(?, ?, ?, ?, ?, ?, ?, ?)}")) {
            cs.setInt(1, paymentPlan.getCustomer().getPersonID());
            cs.setInt(2, paymentPlan.getCar().getCarID());
            cs.setInt(3, paymentPlan.getSalesman().getPersonID());
            cs.setInt(4, paymentPlan.getPlanLength());
            cs.setDouble(5, paymentPlan.getDownPayment()); //MS SQL does not support double
            cs.setDouble(6, paymentPlan.getMonthlyRent());
            cs.setDate(7, paymentPlan.getStartDate());
            cs.setDouble(8, paymentPlan.getFixedCarPrice());
            cs.execute();
        } catch (SQLException e) {
            System.out.println("Could not add payment plan to database: " + e.getMessage());
        }
    }

    /**
     * Fetch a payment plan object from the paymentplan table.
     * @param id ID of the plan to fetch
     * @return {@link PaymentPlan} object
     */
    public static PaymentPlan get(int id) {
        //Cannot proceed if connection isn't established
        if (!hasConnection()) {
            openConnection();
        }

        PaymentPlan paymentPlan = null;
        //Make a call to the database using the super class method "makeCall" Which calls a stored procedure
        try (CallableStatement cs = makeCall("{CALL paymentPlan_getFromDB_ByID(?)}")) {
            cs.setInt(1, id);
            ResultSet resultset = cs.executeQuery();

            //Get first element from call result
            try {
                paymentPlan = createFromResultSet(resultset).get(0);
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("No payment plans found!");
            }
        } catch (SQLException e) {
            System.out.println("Could not get payment plan from database: " + e.getMessage());
        }

        return paymentPlan;
    }

    /**
     * Fetch all payment plans from the paymentplan table.
     * @return ArrayList of {@link PaymentPlan} objects
     */
    public static ArrayList<PaymentPlan> getAll() {
        //Cannot proceed if connection isn't established
        if (!hasConnection()) {
            openConnection();
        }

        ArrayList<PaymentPlan> list = null;
        //Make a call to the database using the super class method "makeCall" Which calls a stored procedure
        try(CallableStatement cs = makeCall("{CALL paymentplan_getAll}")) {
            ResultSet resultset = cs.executeQuery();

            list = createFromResultSet(resultset);

        } catch (SQLException e) {
            System.out.println("Could not get all payment plans from database - " + e.getMessage());
        }

        return list;
    }

    /**
     * Update a payment plan in the paymentplan table by overwriting its data with a new plan.
     * @param paymentPlan {@link PaymentPlan} object with new data
     */
    public static void update(PaymentPlan paymentPlan) {
        //Cannot proceed if connection isn't established
        if (!hasConnection()) {
            openConnection();
        }
        //Make a call to the database using the super class method "makeCall" Which calls a stored procedure
        try (CallableStatement cs = makeCall("{CALL paymentplan_updatePaymentPlan_ByID(?, ?, ?, ?, ?, ?, ?, ?, ?)}")) {
            cs.setInt(1, paymentPlan.getId());
            cs.setInt(2, paymentPlan.getCustomer().getPersonID());
            cs.setInt(3, paymentPlan.getCar().getCarID());
            cs.setInt(4, paymentPlan.getSalesman().getPersonID());
            cs.setInt(5, paymentPlan.getPlanLength());
            cs.setDouble(6, paymentPlan.getDownPayment()); //MS SQL does not support double
            cs.setDouble(7, paymentPlan.getMonthlyRent());
            cs.setDate(8, paymentPlan.getStartDate());
            cs.setDouble(9, paymentPlan.getFixedCarPrice());

            cs.execute();
        } catch (SQLException e) {
            System.out.println("Could not update payment plan in the database - " + e.getMessage());
        }
    }

    /**
     * Delete a payment plan from the paymentplan table.
     * @param id ID of the payment plan to delete
     */
    public static void delete(int id) {
        //Cannot proceed if connection isn't established
        if (!hasConnection()) {
            openConnection();
        }
        //Make a call to the database using the super class method "makeCall" Which calls a stored procedure
        try (CallableStatement cs = makeCall("{CALL paymentplan_deletePaymentPlan_ByID(?)}")) {
            cs.setInt(1, id);

            cs.execute();
        } catch (SQLException e) {
            System.out.println("Could not delete payment plan from database - " + e.getMessage());
        }
    }

    /**
     * Construct list of {@link PaymentPlan} objects from a result set.
     *
     * @param resultset ResultSet from SQL procedure call.
     * @return ArrayList of Customer objects.
     * @throws SQLException
     */
    protected static ArrayList<PaymentPlan> createFromResultSet(ResultSet resultset) throws SQLException {
        ArrayList<PaymentPlan> list = new ArrayList<>();

        //Loop through all results from the set and constructs DBO's from it
        while (resultset.next()) {
            int payment_plan_id = resultset.getInt("payment_plan_id");
            int customer_id = resultset.getInt("customer_id");
            int car_id = resultset.getInt("car_id");
            int salesman_id = resultset.getInt("employee_id");
            int plan_length = resultset.getInt("plan_length");
            double down_payment = resultset.getDouble("down_payment");
            double monthly_rent = resultset.getDouble("monthly_rent");
            Date start_date = resultset.getDate("start_date");
            double fixedCarPrice = resultset.getDouble("car_fixed_price");

            Customer customer;
            Salesman salesman;
            Car car;

            //Get necessary objects from database using data handlers
            customer = CustomerData.get(customer_id);
            salesman = SalesmanData.get(salesman_id);
            car = CarData.get(car_id);

            //Error reporting
            if (customer == null) {
                System.out.println("Could not properly construct payment plan from database: Customer is null!");
            }
            if (salesman == null) {
                System.out.println("Could not properly construct payment plan from database: Salesman is null!");
            }
            if (car == null) {
                System.out.println("Could not properly construct payment plan from database: Car is null!");
            }

            //Construct payment plan object
            list.add(new PaymentPlan(
                    payment_plan_id,
                    down_payment,
                    plan_length,
                    monthly_rent,
                    customer,
                    salesman,
                    car,
                    start_date,
                    fixedCarPrice
            ));
        }

        return list;
    }
}
