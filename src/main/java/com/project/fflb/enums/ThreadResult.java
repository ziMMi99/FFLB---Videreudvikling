package com.project.fflb.enums;

/**
 * Returned from a thread operation to catch any errors that might've happened.
 *
 * @author Ebbe
 */
public enum ThreadResult {
    /**
     * Thread completed its task successfully.
     */
    SUCCESS,
    /**
     * An error occurred with the task.
     */
    GENERICEXCEPTION,
    /**
     * An error with file IO occurred with the task.
     */
    IOEXCEPTION,
    /**
     * An error with an API call occurred with the task.
     */
    APIEXCEPTION
}