package server;

import client.S3ClientConfig;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.example.api.RemoteFileServer;
import paxos.Status;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RmiServer {

    public static void main(String[] args){
        try {
            int port = Integer.parseInt(args[0]);
            String cHost = args[1];
            int cPort = Integer.parseInt(args[2]);
            String bucketName = args[3];
            String otherBucket;

            Regions regions = Regions.US_EAST_1;
//
//            switch (bucketName){
//                case "6650-replication-4":
//                    regions = Regions.EU_WEST_3;
//                    break;
//
//                case "6650-replication-1":
//                    regions = Regions.US_EAST_1;
//                    break;
//
//                case "6650-replication-2":
//                    regions = Regions.US_WEST_1;
//                    break;
//
//                case "6650-replication-5":
//                    regions = Regions.SA_EAST_1;
//                    break;
//
//                case "6650-replication-3":
//                    regions = Regions.AP_NORTHEAST_1;
//                    break;
//                default:
//                    throw new IllegalArgumentException("Bucket Not found");
//            }

            AmazonS3 client = S3ClientConfig.configureClient("*****",
                    "******", regions);

            RemoteFileServer fileServer;

            String host = InetAddress.getLocalHost().getHostAddress();

            Registry registry = LocateRegistry.createRegistry(port);

//
//            if (args.length == 5) {
//
//                otherBucket = args[4];
//                fileServer =
//                        new RemoteFileServerImpl(client, port, cHost, cPort, bucketName, otherBucket);
//                registry.bind("rmi://" + host + ":" + port + "/RemoteFileServer", fileServer);
//                System.out.println("RemoteFileServer is ready and waiting for client connections..., " +
//                        "and bind on the address: " + "rmi://" + host + ":" + port + "/RemoteFileServer" );
//
//                fileServer.setCoordinator(cHost, cPort);
//
//            }else {
//                fileServer = new RemoteFileServerImpl(client, port, cHost, cPort, bucketName);
//                registry.bind("rmi://" + host + ":" + port + "/RemoteFileServer", fileServer);
//                System.out.println("RemoteFileServer is ready and waiting for client connections..., " +
//                        "and bind on the address: " + "rmi://" + host + ":" + port + "/RemoteFileServer" );
//                fileServer.setCoordinator(cHost, cPort);
//
//
//            }
//
//
//            while (true) {
//                Thread.sleep(500);
//                Status status = fileServer.getStatus();
//                System.out.println("Life Cycle: " + status);
//
//                if (status == Status.CRASHED || status == Status.ALL_CRASHED) {
//                    String bucketCopy = fileServer.getBucketToCopy();
//                    UnicastRemoteObject.unexportObject(fileServer, true);
//                    if (status == Status.CRASHED) {
//                        System.out.println("Crashed, ready to restart");
//                        registry.unbind("rmi://" + host + ":" + port + "/RemoteFileServer");
//
//                        fileServer = new RemoteFileServerImpl(client, port, cHost, cPort, bucketName, bucketCopy);
//                        registry.bind("rmi://" + host + ":" + port + "/RemoteFileServer", fileServer);
//                        System.out.println("RemoteFileServer is ready and waiting for client connections..., " +
//                                "and bind on the address: " + "rmi://" + host + ":" + port + "/RemoteFileServer" );
//                        fileServer.setCoordinator(cHost, cPort);
//
//                    } else {
//                        System.out.println("Crashed, ready to restart");
//                        registry.unbind("rmi://" + host + ":" + port + "/RemoteFileServer");
//
//                        fileServer = new RemoteFileServerImpl(client, port, cHost, cPort, bucketName);
//                        registry.bind("rmi://" + host + ":" + port + "/RemoteFileServer", fileServer);
//                        System.out.println("RemoteFileServer is ready and waiting for client connections..., " +
//                                "and bind on the address: " + "rmi://" + host + ":" + port + "/RemoteFileServer" );
//                        fileServer.setCoordinator(cHost, cPort);
//
//                    }
//                }
//            }

            String serviceName;
            RemoteFileServer stub;

            if (args.length == 5) {
                otherBucket = args[4];
                fileServer = new RemoteFileServerImpl(client, port, cHost, cPort, bucketName, otherBucket);
                serviceName = "rmi://" + host + ":" + port + "/RemoteFileServer";
                Naming.bind(serviceName, fileServer);
                System.out.println("RemoteFileServer is ready and waiting for client connections..., " +
                        "and bind on the address: " + serviceName);
                fileServer.setCoordinator(cHost, cPort);
            } else {
                fileServer = new RemoteFileServerImpl(client, port, cHost, cPort, bucketName);
                serviceName = "rmi://" + host + ":" + port + "/RemoteFileServer";

                Naming.bind(serviceName, fileServer);
                System.out.println("RemoteFileServer is ready and waiting for client connections..., " +
                        "and bind on the address: " + serviceName);
                fileServer.setCoordinator(cHost, cPort);
            }
//            stub = fileServer;

            // Export the remote object and bind it to the registry using Name.bind
//            RemoteFileServer stub = (RemoteFileServer) UnicastRemoteObject.exportObject(fileServer, 0);



            while (true) {
                Thread.sleep(500);
                Status status = fileServer.getStatus();
//                System.out.println("Life Cycle: " + status);

                if (status == Status.CRASHED || status == Status.ALL_CRASHED) {
                    System.out.println(status);
                    String bucketCopy = fileServer.getBucketToCopy();
                    UnicastRemoteObject.unexportObject(fileServer, true);

                    if (status == Status.CRASHED) {
                        System.out.println("Crashed, ready to restart");
                        Naming.unbind(serviceName);

                        fileServer = new RemoteFileServerImpl(client, port, cHost, cPort, bucketName, bucketCopy);
//                        stub = (RemoteFileServer) UnicastRemoteObject.exportObject(fileServer, 0);
                        Naming.bind(serviceName, fileServer);

                        System.out.println("RemoteFileServer is ready and waiting for client connections..., " +
                                "and bind on the address: " + serviceName);
                        fileServer.setCoordinator(cHost, cPort);
                    } else {
                        System.out.println("Crashed, ready to restart");
                        Naming.unbind(serviceName);

                        fileServer = new RemoteFileServerImpl(client, port, cHost, cPort, bucketName);
//                        stub = (RemoteFileServer) UnicastRemoteObject.exportObject(fileServer, 0);
                        Naming.bind(serviceName, fileServer);

                        System.out.println("RemoteFileServer is ready and waiting for client connections..., " +
                                "and bind on the address: " + serviceName);
                        fileServer.setCoordinator(cHost, cPort);
                    }
                }
            }

        }catch (RemoteException e){
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (AlreadyBoundException e) {
            throw new RuntimeException(e);
        } catch (NotBoundException e) {
            throw new RuntimeException(e);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
