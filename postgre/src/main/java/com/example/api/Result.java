package com.example.api;

import java.io.Serializable;

/**
 * Represents a Result object containing the outcome of a distributed consensus process.
 * The Result object includes a status and a message describing the outcome.
 */
public class Result implements Serializable {
    private static final long serialVersionUID = 1L;
    private Status status;

    private String message;


    /**
     * Constructs a new Result with the given status and message.
     *
     * @param status  The status of the Result.
     * @param message The message describing the outcome of the Result.
     */
    public Result(Status status, String message) {
        this.status = status;
        this.message = message;
    }

    /**
     * Retrieves the status of the Result.
     *
     * @return The status of the Result.
     */

    public Status getStatus() {
        return status;
    }

    /**
     * Sets the status of the Result.
     *
     * @param status The new status for the Result.
     */

    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Retrieves the message associated with the Result.
     *
     * @return The message describing the outcome of the Result.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message associated with the Result.
     *
     * @param message The new message describing the outcome of the Result.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Returns a string representation of the Result object.
     *
     * @return A string representation of the Result object.
     */
    @Override
    public String toString() {
        return "Result{" +
                "status=" + status +
                ", message='" + message + '\'' +
                '}';
    }
}
