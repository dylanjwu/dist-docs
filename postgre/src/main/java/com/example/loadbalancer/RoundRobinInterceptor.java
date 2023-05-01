package com.example.loadbalancer;

import com.example.api.RemoteFileServer;
import com.example.file.FileController;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.HandlerInterceptor;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class RoundRobinInterceptor implements HandlerInterceptor {

    private int currentServerIndex = 0;
    private String[] serverList = new String[]{"my-server-1", "my-server-2", "my-server-3", "my-server-4", "my-server-5"};
    private String[] serverPort = new String[]{"1111", "1112", "1113", "1114", "1115"};
//    private String[] serverList = new String[]{"localhost", "localhost", "localhost", "localhost", "localhost"};

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        int index = getNextServerIndex();
        // Perform round-robin logic with the index
        try {
            String url = "rmi://" + InetAddress.getByName(serverList[index]).getHostAddress() + ":" + serverPort[index] + "/RemoteFileServer";
            System.out.println(url);

            RemoteFileServer remoteFileServer = (RemoteFileServer) Naming.lookup(url);

            // Save the remoteFileServer instance to be used in the controller
            request.setAttribute("remoteFileServer", remoteFileServer);

        } catch (UnknownHostException | MalformedURLException | NotBoundException | RemoteException e) {
            throw new RuntimeException(e);
        }
        return true;

    }

    public int getNextServerIndex() {
        int index = currentServerIndex;
        currentServerIndex = (currentServerIndex + 1) % serverList.length;
        return index;
    }
}
