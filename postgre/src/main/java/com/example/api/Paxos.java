package com.example.api;



import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Paxos extends Remote {

    /**
     * Receives a proposal and returns a promise for the given proposal.
     *
     * @param proposal The proposal to be considered.
     * @return A Promise object containing the status and the proposal.
     * @throws RemoteException If an error occurs during remote method invocation.
     */
    Promise promise(Proposal proposal) throws RemoteException;

    /**
     * Accepts or rejects the given proposal.
     *
     * @param proposal The proposal to be accepted or rejected.
     * @return A Boolean value indicating whether the proposal was accepted (true) or rejected (false).
     * @throws RemoteException If an error occurs during remote method invocation.
     */

    Boolean accept(Proposal proposal) throws RemoteException;

    /**
     * Learns the result of a given proposal.
     *
     * @param proposal The proposal to be learned.
     * @return A Result object containing the status and the message.
     * @throws RemoteException If an error occurs during remote method invocation.
     */

    Result learn(Proposal proposal) throws  RemoteException;

    /**
     * Recovers the state of the Paxos object from a remote Paxos object with the given address and port.
     *
     * @param address The address of the remote Paxos object.
     * @param port    The port number of the remote Paxos object.
     * @throws RemoteException If an error occurs during remote method invocation.
     */
    void recover(String srcBucket) throws RemoteException;

    String getBucket() throws RemoteException;

    String getHost() throws RemoteException;

    int getPort() throws RemoteException;

    void setCoordinator(String host, int port) throws RemoteException;


}
