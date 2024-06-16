package com.project.fflb.dbo;

import api.com.ferrari.finances.dk.rki.Rating;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Holds information about a customer.
 *
 * @author Victor
 */
public class Customer extends Person {
    // ===============
    //   VARIABLES
    // ===============
    /**
     * The address of the customer
     */
    private String address;
    /**
     * The post code of the customer
     */
    private int postCode;
    /**
     * The CPR of the customer
     */
    private String CPR;
    /**
     * How the customer is rated by RKI. Possible ratings are A, B, C or D.
     * Customers with rating D will not be considered for payment plans.
     */
    private Rating creditScore;

    // ===============
    //   CONSTRUCTORS
    // ===============

    //Constructor using ID
    public Customer(int personID, String firstName, String lastName, String email, int phoneNumber, String address, int postCode, String CPR, Rating creditScore) {
        super(personID, firstName, lastName, email, phoneNumber);
        this.address = address;
        this.postCode = postCode;
        this.CPR = CPR;
        this.creditScore = creditScore;
    }

    //Constructor not using ID
    public Customer(String firstName, String lastName, String email, int phoneNumber, String address, int postCode, String CPR, Rating creditScore) {
        super(firstName, lastName, email, phoneNumber);
        this.address = address;
        this.postCode = postCode;
        this.CPR = CPR;
        this.creditScore = creditScore;
    }

    //Copy-constructor
    public Customer(Customer customer) {
        this(customer.getPersonID(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                customer.getPhoneNumber(),
                customer.getAddress(),
                customer.getPostCode(),
                customer.getCPR(),
                customer.getCreditScore()
        );
    }
    // ===============
    //   SETTERS
    // ===============

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPostCode(int postCode) {
        this.postCode = postCode;
    }

    public void setCPR(String CPR) {
        this.CPR = CPR;
    }

    public void setCreditScore(Rating creditScore) {
        this.creditScore = creditScore;
    }

    // ===============
    //   GETTERS
    // ===============

    public String getAddress() {
        return address;
    }

    public int getPostCode() {
        return postCode;
    }

    public String getCPR() {
        return CPR;
    }

    public Rating getCreditScore() {
        return creditScore;
    }

    @Override
    public String toString() {
        return  "CPR: " + getCPR() +
                " | Name: " + getFirstName() + " " + getLastName() +
                " | Address: " + getAddress() +
                " | Rating: " + getCreditScore();
    }

    /**
     * Static method to sort a list of customers by their credit score.
     * Customers with rating A come first, B next etc...
     * @param list List to sort.
     */
    public static void sortCustomerListByRating(ArrayList<Customer> list) {
        list.sort(new Comparator<>() {
            @Override
            public int compare(Customer c1, Customer c2) {
                //Get the ordinal (number) values of the enum such that they can be compared
                int cr1 = c1.getCreditScore().ordinal();
                int cr2 = c2.getCreditScore().ordinal();

                //Compare the numbers
                return Integer.compare(cr1, cr2);
            }
        });
    }
}
