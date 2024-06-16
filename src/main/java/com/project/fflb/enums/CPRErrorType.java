package com.project.fflb.enums;

/**
 * Result from parsing a CPR number string.
 * @author Simon
 */
public enum CPRErrorType {
    /**
     * The entered CPR number is the wrong length
     */
    INVALID_LENGTH,
    /**
     * The entered CPR number has an invalid date
     */
    INVALID_DATE,
    /**
     * Valid CPR
     */
    SUCCESS
}
