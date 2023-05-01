package com.example;

import java.net.InetAddress;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.properties")
public class RmiHostConfiguration {

    @Value("${rmi.server.host}")
    private String rmiServerHost;



    @Value("${rmi.server.port}")
    private String rmiServerPort;


    public String getRmiServerHost() {
        return rmiServerHost;
    }

    public void setRmiServerHost(String rmiServerHost) {
        this.rmiServerHost = rmiServerHost;
    }

    public String getRmiServerPort() {
        return rmiServerPort;
    }

    public void setRmiServerPort(String rmiServerPort) {
        this.rmiServerPort = rmiServerPort;
    }

    public String getRmiServerUrl() {
        try {
            String address = InetAddress.getByName(rmiServerHost).getHostAddress();
            String url =  "rmi://" + address + ":" + rmiServerPort + "/RemoteFileServer";
            System.out.println(url);
            return url;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
