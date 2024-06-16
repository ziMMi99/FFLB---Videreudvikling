package com.project.fflb.data.dboData;

import com.project.fflb.data.DataHandler;
import com.project.fflb.dbo.Car;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Handles all database/data handling for the Car class.
 *
 * @author Simon
 */
public class CarData extends DataHandler {
    /**
     * Insert a new car into the car table.
     * @param car Inserted {@link Car} object
     */
    public static void create(Car car) {
        //Cannot proceed if connection isn't established
        if (!hasConnection()) {
            openConnection();
        }

        //Make a call to the database using the super class method "makeCall" Which calls a stored procedure
        try (CallableStatement cs = makeCall("{CALL car_addToDB(?, ?, ?)}")) {
            cs.setString(1, car.getModelName());
            cs.setDouble(2, car.getPrice());
            cs.setInt(3, car.hasPaymentPlan() ? 1 : 0); //Convert boolean to number

            cs.execute();
        } catch (SQLException e) {
            System.out.println("Could not add car to database - " + e.getMessage());
        }
    }

    /**
     * Fetch a car object from the car table.
     * @param id ID of the car to fetch
     * @return {@link Car} object
     */
    public static Car get(int id) {
        //Cannot proceed if connection isn't established
        if (!hasConnection()) {
            openConnection();
        }

        Car car = null;
        //Make a call to the database using the super class method "makeCall" Which calls a stored procedure.
        try (CallableStatement cs = makeCall("{CALL car_getFromDB_ByID(?)}")) {
            cs.setInt(1, id);
            ResultSet resultset = cs.executeQuery();

            //Get first element from call result
            try {
                car = createFromResultSet(resultset).get(0);
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("No cars found!");
            }
        } catch (SQLException e) {
            System.out.println("Could not get car from database - " + e.getMessage());
        }

        return car;
    }

    /**
     * Fetch all cars from the car table.
     * @return ArrayList of {@link Car} objects
     */
    public static ArrayList<Car> getAll() {
        //Cannot proceed if connection isn't established
        if (!hasConnection()) {
            openConnection();
        }

        ArrayList<Car> list = null;
        //Make a call to the database using the super class method "makeCall" Which calls a stored procedure
        try(CallableStatement cs = makeCall("{CALL car_getAll}")) {
            ResultSet resultset = cs.executeQuery();

            list = createFromResultSet(resultset);
        } catch (SQLException e) {
            System.out.println("Could not get all cars from database - " + e.getMessage());
        }

        return list;
    }

    /**
     * Update a car in the car table by overwriting its data with a new car.
     * @param car {@link Car} object with new data
     */
    public static void update(Car car) {
        //Cannot proceed if connection isn't established
        if (!hasConnection()) {
            openConnection();
        }

        //Make a call to the database using the super class method "makeCall" Which calls a stored procedure
        try (CallableStatement cs = makeCall("{CALL car_updateCar_ByID(?, ?, ?, ?)}")) {
            cs.setInt(1, car.getCarID());
            cs.setString(2, car.getModelName());
            cs.setDouble(3, car.getPrice());
            cs.setInt(4, car.hasPaymentPlan() ? 1 : 0); //Convert boolean to int

            cs.execute();
        } catch (SQLException e) {
            System.out.println("Could not update car in the database - " + e.getMessage());
        }
    }

    /**
     * Delete a car from the car table.
     * @param id ID of the car to delete
     */
    public static void delete(int id) {
        //Cannot proceed if connection isn't established
        if (!hasConnection()) {
            openConnection();
        }

        //Make a call to the database using the super class method "makeCall" Which calls a stored procedure
        try (CallableStatement cs = makeCall("{CALL car_deleteCar_ByID(?)}")) {
            cs.setInt(1, id);

            cs.execute();
        } catch (SQLException e) {
            System.out.println("Could not delete car from database - " + e.getMessage());
        }
    }

    /**
     * Construct list of {@link Car} objects from a result set.
     *
     * @param resultset ResultSet from SQL procedure call.
     * @return ArrayList of Customer objects.
     * @throws SQLException
     */
    protected static ArrayList<Car> createFromResultSet(ResultSet resultset) throws SQLException {
        ArrayList<Car> list = new ArrayList<>();

        //Loop through all results from the set and constructs DBO's from it
        while (resultset.next()) {
            int car_id = resultset.getInt("car_id");
            String modelName = resultset.getString("model_name");
            double price = resultset.getDouble("price");
            boolean hasPaymentPlan = resultset.getInt("has_payment_plan") != 0; //Convert int to boolean

            list.add(new Car (car_id, modelName, price, hasPaymentPlan));
        }

        return list;
    }
}
