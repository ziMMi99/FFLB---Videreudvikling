package com.project.fflb.threading.api;

import api.com.ferrari.finances.dk.rki.Rating;

/**
 * Wrapper class for data fetched from an API call.
 * Contains variables for interest rate and credit score, with getters for each.
 *
 * @author Ebbe
 * @Co-Author: Simon
 */
public class APIData {
    private double interest;
    private Rating creditScore;

    /**
     * Constructor for wrapping an interest value.
     * @param interest Interest value (double)
     */
    public APIData(double interest) {
        this.interest = interest;
    }

    /**
     * Constructor for wrapping a credit score value.
     * @param creditScore Credit score value ({@link Rating})
     */
    public APIData(Rating creditScore) {
        this.creditScore = creditScore;
    }

    /**
     * Get interest rate from API call.
     * @return Interest (double)
     */
    public double getInterest() {
        return interest;
    }

    /**
     * Get credit score from API call.
     * @return Credit score {@link Rating}
     */
    public Rating getCreditScore() {
        return creditScore;
    }
}
