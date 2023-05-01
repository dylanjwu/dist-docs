package com.example.user;

import com.example.api.RemoteFileServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

@Configuration
public class AppConfig {

    @Value("${rmi.server.host}")
    private String rmiServerHost;

    @Value("${rmi.server.port}")
    private String port;
    @Bean
    public RemoteFileServer remoteFileServer() {
        try {
            String host = InetAddress.getByName(rmiServerHost).getHostAddress();
            String url = "rmi://" +  host + ":" + port + "/RemoteFileServer";
            return (RemoteFileServer) Naming.lookup(url);
        } catch (RemoteException | NotBoundException | MalformedURLException e) {
            // Handle any exceptions that may occur during the lookup
            e.printStackTrace();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
