package com.project.fflb.enums;

/**
 * Which type of call to use when accessing the APIs.
 *
 * @author Ebbe
 */
public enum APICallType {
    /**
     * Call RKI for information regarding a customer's credit score.
     */
    RKI,
    /**
     * Call the bank for the daily rate of loans.
     */
    BANK
}
