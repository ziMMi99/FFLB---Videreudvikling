package com.project.fflb.threading.api;

import api.com.ferrari.finances.dk.bank.InterestRate;

import java.time.LocalDateTime;

/**
 * This Singleton Class handles fetching the bank's daily changing rate, making sure to only fetch it when needed.
 * Does NOT instantiate threads of its own, so must ideally be threaded externally to handle delays.
 *
 * @author Ebbe
 */
public class BankRateHandler {
    /**
     * The singleton instance
     */
    private static BankRateHandler instance;
    /**
     * The currently stored interest rate
     */
    private double rate;
    /**
     * The last time the interest rate was fetched from the bank API
     */
    private LocalDateTime dateFetched;

    /**
     * Private constructor.
     * When the singleton is first instanced, the rate is fetched for the first time.
     */
    private BankRateHandler() {
        System.out.println("RateHandler instanced, fetching new rate.");
        fetchTodaysRate();
    }

    /**
     * Instantiates its own singleton instance if it is null, then returns it.
     * @return Singleton instance
     */
    public static BankRateHandler i() {
        if (instance == null) {
            instance = new BankRateHandler();
        }

        return instance;
    }

    /**
     * Get the bank's interest rate. If the date changed from the previous time the rate was fetched, it will be fetched from the API again.
     * @return Rate
     */
    public double getRate() {
        if (newDate()) {
            System.out.println("New date, fetching new rate.");
            fetchTodaysRate();
        }

        return rate;
    }

    /**
     * Check if the current date is different from when the rate was last fetched.
     * @return Whether a new date is reached
     */
    private boolean newDate() {
        //Get current system time
        LocalDateTime current = LocalDateTime.now();

        //Is it the same day of the year as the last time we fetched the rate?
        return dateFetched.getDayOfYear() != current.getDayOfYear();
    }

    /**
     * Get the current rate from the bank API and update {@link #dateFetched}.
     */
    private void fetchTodaysRate() {
        rate = InterestRate.i().todaysRate();
        dateFetched = LocalDateTime.now();

    }
}
