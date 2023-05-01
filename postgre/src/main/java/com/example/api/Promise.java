package com.example.api;

import java.io.Serializable;


/**
 * Represents a Promise object used in the server for handling distributed consensus.
 * The Promise object contains a status and a proposal.
 */
public class Promise implements Serializable {

    private Status status;
    private static final long serialVersionUID = 1L;

    private Proposal proposal;
    /**
     * Constructs a new Promise with the given status and proposal.
     *
     * @param status   The status of the Promise.
     * @param proposal The proposal associated with the Promise.
     */


    public Promise(Status status, Proposal proposal) {
        this.status = status;
        this.proposal = proposal;
    }

    /**
     * Retrieves the status of the Promise.
     *
     * @return The status of the Promise.
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Sets the status of the Promise.
     *
     * @param status The new status for the Promise.
     */
    public void setStatus(Status status) {
        this.status = status;
    }


    /**
     * Retrieves the proposal associated with the Promise.
     *
     * @return The proposal associated with the Promise.
     */
    public Proposal getProposal() {
        return proposal;
    }


    /**
     * Sets the proposal associated with the Promise.
     *
     * @param proposal The new proposal for the Promise.
     */
    public void setProposal(Proposal proposal) {
        this.proposal = proposal;
    }

    /**
     * Returns a string representation of the Promise object.
     *
     * @return A string representation of the Promise object.
     */
    @Override
    public String toString() {
        return "Promise{" +
                "status=" + status +
                ", proposal=" + proposal +
                '}';
    }
}