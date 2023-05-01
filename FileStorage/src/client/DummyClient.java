package client;

import com.example.api.RemoteFileServer;

import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class DummyClient {

    public static void main(String[] args) {
        String host;
        int port;
        try{
            host = args[0];
            port = Integer.parseInt(args[1]);

            if (host.equals("localhost")){
                host = InetAddress.getLocalHost().getHostAddress();
            }

            Registry registry = LocateRegistry.getRegistry(host, port);

            RemoteFileServer fileServer = (RemoteFileServer) registry.lookup("rmi://" + host + ":" + port + "/RemoteFileServer");


//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    while(true){
//                        registry.lookup("rmi://" + host + ":" + port + "/RemoteFileServer"( )
//
//                    }
//                }
//            }).start();



            String file = "test6.txt";
//            fileServer.createFile(file,  1L, new byte[0]);

            // Call the updateFile method
            Scanner scanner = new Scanner(System.in);
            String line;
            while(!(line = scanner.nextLine()).equalsIgnoreCase("Q")) {
                try {
                    byte[] updatedFileBytes = line.getBytes(StandardCharsets.UTF_8);
                    System.out.println(fileServer.updateFile(file, updatedFileBytes, 1L));

                    byte[] fileContentsBytes = fileServer.readFile(file, 1L);
                    String fileContents = new String(fileContentsBytes, StandardCharsets.UTF_8);
                    System.out.println("File contents: " + fileContents);
                }catch (Exception e) {
                    System.out.println("Please input again, the object is refreshed");
                    fileServer = (RemoteFileServer) registry.lookup("rmi://" + host + ":" + port + "/RemoteFileServer");
                }
            }

            // Call the readFile method
            byte[] fileContentsBytes = fileServer.readFile(file, 1L);
            String fileContents = new String(fileContentsBytes, StandardCharsets.UTF_8);
            System.out.println("File contents: " + fileContents);
            // Call the delete method
            boolean isDeleted = fileServer.deleteFile(file, 1L);
            System.out.println("File deleted: " + isDeleted);

        }catch (Exception e){
            e.printStackTrace();
        }
        // Call the createFile method
    }

}
