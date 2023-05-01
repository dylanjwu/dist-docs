package com.example.api;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteFileServer extends Remote, Paxos, TwoPC {
    boolean createFile(String fileName, long userId, byte[] contents) throws RemoteException;

    byte[] readFile(String fileName, long userId) throws RemoteException;

    // update an existing file with the specified user
    boolean updateFile(String fileName, byte[] fileContents, long userId) throws RemoteException;

    // delete an existing file with the specified user
    boolean deleteFile(String fileName, long userId) throws RemoteException;

}
