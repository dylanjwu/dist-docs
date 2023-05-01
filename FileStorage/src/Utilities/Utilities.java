package Utilities;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;

public class Utilities {
    /**
     * Returns the current time formatted as a String.
     *
     * @return The current time as a formatted String.
     */
    private static String getTime(){
        LocalDateTime time = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss");
        return time.format(formatter);
    }

    /**
     * Logs a message to the standard output with a specified entity and the current time.
     *
     * @param entity The name or identifier of the entity associated with the log message.
     * @param message The log message to be printed.
     */

    public static void log(String entity, String message){
        System.out.println(entity + " > " + message + " > " + getTime());
    }



    /**
     * Checks if the specified IP address and port are reachable within the given timeout.
     *
     * @param ipAddress    The IP address to check for reachability.
     * @param port         The port number to check for reachability.
     * @param timeoutMillis The timeout for the reachability check in milliseconds.
     * @return true if the IP address and port are reachable within the specified timeout,
     *         false otherwise.
     */
    public static boolean isReachable(String ipAddress, int port, int timeoutMillis) {
        try (Socket socket = new Socket()) {
            InetSocketAddress socketAddress = new InetSocketAddress(ipAddress, port);
            socket.connect(socketAddress, timeoutMillis);
            return true;
        } catch (IOException e) {
            return false;
        }
    }


}
