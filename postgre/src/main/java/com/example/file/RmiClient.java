package com.example.file;

import com.example.RmiHostConfiguration;
import com.example.api.RemoteFileServer;
import com.example.entity.File;
import com.example.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Set;

@Service
public class RmiClient {

//    @Value("${rmi.server.url}")
//    private String rmiServerUrl;
//    @Value("${rmi.server.host}")
//    private String host;
//
//    @Value("${rmi.server.port}")
//    private int port;
//
//    @Value("${rmi.server.url}")
//    private String rmiServerUrl;

//    @Autowired
//    private RmiHostConfiguration rmiHostConfiguration;
//
//
//
//    @Autowired
//    private RemoteFileServer remoteFileServer;

//    @Scheduled(fixedRate = 6000) // Run every 60 seconds
//    public void refreshRemoteFileServer() {
//        try {
//            ((RmiProxyFactoryBean) remoteFileServer).afterPropertiesSet();
//        } catch (Exception e) {
//            // Handle any exceptions that may occur during the refresh
//            e.printStackTrace();
//        }
//    }

//    @PostConstruct
//    public void init() {
//        refreshRemoteFileServer();
//    }
//
//    @Scheduled(fixedRate = 6000) // Run every 6 seconds
//    public void refreshRemoteFileServer() {
//        try {
//            remoteFileServer = (RemoteFileServer) Naming.lookup(rmiHostConfiguration.getRmiServerUrl());
//        } catch (RemoteException | NotBoundException | MalformedURLException e) {
//            // Handle any exceptions that may occur during the refresh
//            e.printStackTrace();
//        }
//    }

//    @PostConstruct
//    public void init() throws RemoteException, NotBoundException {
//        Registry registry = LocateRegistry.getRegistry(host, port);
//        remoteFileServer = (RemoteFileServer) registry.lookup(rmiServerUrl);
//    }

//    @PostConstruct
//    public void init() throws NotBoundException, RemoteException {
//        Registry registry = null;
//        while (true){
//            try {
//                registry = LocateRegistry.getRegistry(host, 2048);
//                remoteFileServer = (RemoteFileServer) registry.lookup(rmiServerUrl);
//                break;
//            } catch (Exception e) {
//                //handle exception
//                remoteFileServer = null;
//            }
//        }
//    }

//    public boolean createFile(FileRequest fileRequest) throws RemoteException {
//        String fileName = fileRequest.getFilename();
//        List<Long> users = fileRequest.getSharedList();
//        byte[] contents = fileRequest.getContents().getBytes();
//
//        for (Long user : users) {
//            if  (!this.remoteFileServer.createFile(fileName, user,  contents)) {
//                return false;
//            };
//        }
//        return true;
//    }
//
//    public boolean deleteFile(String fileName, List<Long> users) throws RemoteException {
//        for (Long user : users) {
//            if  (!this.remoteFileServer.deleteFile(fileName, user)) {
//                return false;
//            };
//        }
//        return true;
//    }
//
//    public byte[] readFile(String fileName, Long user) throws RemoteException {
//        return this.remoteFileServer.readFile(fileName, user);
//    }
//
//    public boolean updateFile(File fileRequest, String contents) throws RemoteException {
//
//        String fileName = fileRequest.getFileName();
//        Set<User> users = fileRequest.getUsers();
//
//        for (User user:
//             users) {
//
//            if(! this.remoteFileServer.updateFile(fileName, contents.getBytes(StandardCharsets.UTF_8), user.getId())){
//                return false;
//            }
//
//        }
//        return true;
//
//    }

    public boolean createFile(RemoteFileServer remoteFileServer, FileRequest fileRequest) throws RemoteException {
        String fileName = fileRequest.getFilename();
        List<Long> users = fileRequest.getSharedList();
        byte[] contents = fileRequest.getContents().getBytes();

        for (Long user : users) {
            if  (!remoteFileServer.createFile(fileName, user,  contents)) {
                return false;
            };
        }
        return true;
    }

    public boolean deleteFile(RemoteFileServer remoteFileServer, String fileName, List<Long> users) throws RemoteException {
        for (Long user : users) {
            if  (!remoteFileServer.deleteFile(fileName, user)) {
                return false;
            };
        }
        return true;
    }

    public byte[] readFile(RemoteFileServer remoteFileServer, String fileName, Long user) throws RemoteException {
        return remoteFileServer.readFile(fileName, user);
    }

    public boolean updateFile(RemoteFileServer remoteFileServer, File fileRequest, String contents) throws RemoteException {

        String fileName = fileRequest.getFileName();
        Set<User> users = fileRequest.getUsers();

        for (User user:
                users) {

            if(!remoteFileServer.updateFile(fileName, contents.getBytes(StandardCharsets.UTF_8), user.getId())){
                return false;
            }

        }
        return true;

    }

}
