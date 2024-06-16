package com.project.fflb.dbo;

/**
 * Holds information about a salesman
 *
 * @author Rasmus
 */

public class Salesman extends Person {

    // ===============
    //   VARIABLES
    // ===============
    /**
     * The percentage of the sales profit that goes to the seller.
     */
    private double salesmanLoanLimit;
    /**
     * Whether the salesman is active within the organization.
     */
    private boolean isActive;

    // ===============
    //   CONSTRUCTORS
    // ===============

    //Constructor with personID
    public Salesman(int personID, String firstName, String lastName, String email, int phoneNumber, double salesmanLoanLimit, boolean isActive){
        super(personID, firstName, lastName, email, phoneNumber);
        this.salesmanLoanLimit = salesmanLoanLimit;
        this.isActive = isActive;
    }
    //Constructor without personID
    public Salesman(String firstName, String lastName, String email, int phoneNumber, double salesmanLoanLimit, boolean isActive){
        super(firstName, lastName, email, phoneNumber);
        this.salesmanLoanLimit = salesmanLoanLimit;
        this.isActive = isActive;
    }

    //Copy constructor
    public Salesman(Salesman salesman){
        this(salesman.getPersonID(),
                salesman.getFirstName(),
                salesman.getLastName(),
                salesman.getEmail(),
                salesman.getPhoneNumber(),
                salesman.getSalesmanLoanLimit(),
                salesman.getIsActive()
        );
    }

    // ===========
    //   SETTERS
    // ===========
    public void setSalesmanLoanLimit(double salesmanLoanLimit){
        this.salesmanLoanLimit = salesmanLoanLimit;
    }

    public void setIsActive(boolean isActive){
        this.isActive = isActive;
    }

    // ===========
    //   GETTERS
    // ===========
    public double getSalesmanLoanLimit() {
        return salesmanLoanLimit;
    }

    public boolean getIsActive(){
        return isActive;
    }

    @Override
    public String toString() {
        return getFirstName() + " " + getLastName() +
                " - Phone Number: " + getPhoneNumber() +
                " - Email: " + getEmail();
    }
}