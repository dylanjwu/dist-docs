package com.example.api;



import paxos.Result;
import paxos.Task;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * The Coordinator interface represents the remote methods that can be invoked on a Coordinator
 * object. This interface extends the Remote interface, which is required for RMI.
 */
public interface Coordinator extends Remote {

   /**
    * Adds an acceptor to the coordinator with the specified host and port.
    *
    * @param host The host address of the acceptor.
    * @param port The port number on which the acceptor is listening.
    * @throws RemoteException If an error occurs during remote method invocation.
    */
   void addAcceptor(String host,  int port) throws RemoteException;


   /**
    * Executes the given task using the coordinator and returns the result.
    *
    * @param task The task to be executed.
    * @return A Result object containing the status and message of the task execution.
    * @throws RemoteException If an error occurs during remote method invocation.
    */
   Result execute(Task task) throws RemoteException;


   Result twoPcSend(Task task) throws RemoteException;



}
