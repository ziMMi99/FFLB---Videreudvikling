package com.project.fflb.dbo;

/**
 * Holds information about a car.
 *
 * @author Sebastian
 */
public class Car {
    // ===============
    //   VARIABLES
    // ===============
    /**
     * ID equivalent to the primary key of the entity in the database
     */
    private int carID;
    private double price;
    private String modelName;
    /**
     * Whether the car is being sold as part of a payment plan already.
     * This is to prevent a car being sold to two customers at once.
     */
    private boolean hasPaymentPlan;

    // ===============
    //   CONSTRUCTORS
    // ===============

    //Constructor with ID
    public Car(int carID, String modelName, double price, boolean hasPaymentPlan){
        this.carID = carID;
        this.modelName = modelName;
        this.price = price;
        this.hasPaymentPlan = hasPaymentPlan;
    }

    //Constructor without ID
    public Car(String modelName, double price, boolean hasPaymentPlan) {
        this.modelName = modelName;
        this.price = price;
        this.hasPaymentPlan = hasPaymentPlan;
    }

    //Copy-constructor
    public Car(Car car){
        this.carID = car.carID;
        this.modelName = car.modelName;
        this.price = car.price;
        this.hasPaymentPlan = car.hasPaymentPlan;
    }

    // ===============
    //   SETTERS
    // ===============
    public void setCarID(int carID){
        this.carID = carID;
    }

    public void setModelName(String modelName){
        this.modelName = modelName;
    }

    public void setPrice(double price){
        this.price = price;
    }

    public boolean hasPaymentPlan() {
        return hasPaymentPlan;
    }

    // ===============
    //   GETTERS
    // ===============
    public int getCarID(){
        return carID;
    }

    public String getModelName(){
        return modelName;
    }

    public double getPrice(){
        return price;
    }

    public void setHasPaymentPlan(boolean hasPaymentPlan) {
        this.hasPaymentPlan = hasPaymentPlan;
    }

    //Other Methods
    @Override
    public String toString() {
        return String.format("Model Name: %s | Price: %.2f kr", this.modelName, this.price);
    }
}
