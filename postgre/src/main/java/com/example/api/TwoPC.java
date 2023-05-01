package com.example.api;


import java.rmi.Remote;
import java.rmi.RemoteException;

public interface TwoPC extends Remote {

    Result startPrepare(Task task) throws RemoteException;
    Result startCommit(Task task) throws RemoteException;


}
