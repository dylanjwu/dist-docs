package coordinator;

import com.example.api.Coordinator;

import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.RemoteException;


/**
 * The CoordinatorDriver class is the entry point for the coordinator in the RPC Key-Value system.
 * It starts a coordinator instance and sets up the required connections.
 */
public class CoordinatorDriver {

    /**
     * The main method for the CoordinatorDriver class.
     * Initializes and starts a coordinator with the specified port number.
     *
     * @param args The command-line arguments. Expects a single argument: the port number.
     */
    public static void main(String[] args) {

        if (args.length != 1){
            throw new IllegalArgumentException("The args' size should be 1, please provide your port number");
        }
        try {
            int port = Integer.parseInt(args[0]);
            Coordinator coordinator = new CoordinatorImp(port);

        }catch (NumberFormatException e){
            throw new NumberFormatException("Please input a valid port number");
        } catch (UnknownHostException | RemoteException | MalformedURLException e) {
            throw new RuntimeException(e);
        }

    }
}
