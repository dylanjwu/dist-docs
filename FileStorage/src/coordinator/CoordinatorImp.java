package coordinator;

import Utilities.Utilities;
import com.example.api.Coordinator;
import com.example.api.RemoteFileServer;
import paxos.*;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * The CoordinatorImp class is the implementation of the Coordinator interface.
 * It manages the connections between clients and servers, and coordinates the
 * execution of tasks using the Paxos consensus algorithm.
 */
public class CoordinatorImp extends UnicastRemoteObject implements Coordinator {

    private Map<String, RemoteFileServer> serverMap;
    private String id;
    private String hostName;
    private int port;

    private ExecutorService executorService;

    public CoordinatorImp(int port) throws UnknownHostException, RemoteException, MalformedURLException {
        this.port = port;
        this.hostName = InetAddress.getLocalHost().getHostAddress();
        this.id = hostName + ":" + this.port;
        this.executorService = Executors.newFixedThreadPool(10);
        this.serverMap = new HashMap<>();
        Registry registry = LocateRegistry.createRegistry(port);
        Naming.rebind("rmi://" + this.id + "/Coordinator",  this);
        Utilities.log(this.id, "Coordinator has been bind at this url: " +" rmi://" + this.id + "/Coordinator");
//        registry.rebind("rmi://" + this.id + "/Coordinator",  this);
//        Utilities.log(this.id, "Coordinator has been bind at this url: " +" rmi://" + this.id + "/Coordinator");
    }


    /**
     * Adds a server as an acceptor to the coordinator.
     *
     * @param host The host address of the server.
     * @param port The port number of the server.
     * @throws RemoteException if there's an issue with remote communication.
     */

    @Override
    public void addAcceptor(String host, int port) throws RemoteException {

        try {
            host = InetAddress.getByName(host).getHostAddress();
            String serverId  =  host + ":" + port;
            System.out.println( host + ":" + port);
            Registry registry = LocateRegistry.getRegistry(host, port);
//            RemoteFileServer newServer = (RemoteFileServer) registry.lookup("rmi://" +serverId + "/RemoteFileServer");
            RemoteFileServer newServer = (RemoteFileServer) Naming.lookup("rmi://" +serverId + "/RemoteFileServer");

            serverId = serverId + ":" + newServer.getBucket();


            Utilities.log(this.id, "A server has added to the serverMap: " + serverId );
            this.serverMap.put(serverId, newServer);

        } catch (RemoteException e) {
            throw new RemoteException("cannot find the server's remote object");
        } catch (NotBoundException e) {
            throw new RuntimeException("cannot find the server's remote object");
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Executes a task by coordinating with acceptors using the Paxos consensus algorithm.
     * It goes through the prepare, accept, and learn phases of the Paxos algorithm and returns
     * the final result of the task execution.
     *
     * @param task The task to be executed.
     * @return Result of the task execution.
     * @throws RemoteException if there's an issue with remote communication.
     */
    @Override
    public Result execute(Task task) throws RemoteException {
        // generate the proposal first
        Proposal proposal = Proposal.createProposal(task);
         // phase 0: get the acceptor's list
        int qualifiedNum = Math.floorDiv(this.serverMap.size(), 2) + 1;
        int promisedNum = 0;

        // phase1: send the "prepare" and receive promises
        for (RemoteFileServer acceptor: serverMap.values()){
                Promise promise = acceptor.promise(proposal);

                if (promise.getStatus() == Status.REJECT) {
                    Utilities.log(this.id, "the server: " + acceptor.getHost() + ":" + acceptor.getPort()
                            + "is failed during the preparation");
                } else {
                    promisedNum++;
                    Utilities.log(this.id, "the server " + acceptor.getHost() + ":" + acceptor.getPort()
                            + "is promised during the preparation");
                }

        }


        if (promisedNum < qualifiedNum ){
            return new Result(Status.REJECT, "Not enough acceptor promised");
        }


        // phase2: send accept
        int accepted = 0;

        for (RemoteFileServer acceptor: serverMap.values()){
            Boolean res = acceptor.accept(proposal);
            if (!res) {
                Utilities.log(this.id, "the server: " + acceptor.getHost() + ":" + acceptor.getPort()
                        + "is failed during the accept process");
            } else {
                accepted++;
                Utilities.log(this.id, "the server " + acceptor.getHost() + ":" + acceptor.getPort()
                        + "is accepted");
            }
        }

        if (accepted < qualifiedNum){
            return new Result(Status.REJECT, "Not enough acceptor accepted");
        }

        // phase3: all acceptor should learn the proposal
        List<RemoteFileServer> learnedAcceptor = new ArrayList<>();
        List<RemoteFileServer> unlearnedAcceptor = new ArrayList<>();
        List<Result> acceptedResult = new ArrayList<>();
        List<Result> declinedResult = new ArrayList<>();

        for (RemoteFileServer acceptor: serverMap.values()){
            Result result = acceptor.learn(proposal);
            if (result.getStatus() == Status.REJECT){
                Utilities.log(this.id, "the server: " + acceptor.getHost() + ":" + acceptor.getPort()
                        + "is failed during the learning process, it will be recover later");
                unlearnedAcceptor.add(acceptor);
                declinedResult.add(result);
            }else{
                Utilities.log(this.id, "the server: " + acceptor.getHost() + ":" + acceptor.getPort()
                        + "is learned ");
                learnedAcceptor.add(acceptor);
                acceptedResult.add(result);
            }
        }

        // restart the crushed servers, and sync their buckets
        recoverMode(unlearnedAcceptor, learnedAcceptor);
        //sleep for a while to waiting the restart of server

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (learnedAcceptor.size() == 0){
            return declinedResult.get(0);
        }

        return acceptedResult.get(0);
    }

    // todo: still need to complete the recover mode
    private boolean recoverMode(List<RemoteFileServer> badServers, List<RemoteFileServer> goodServers) throws RemoteException {

        if (goodServers.size() == 0){
            System.out.println("no server is alive, let the client resend the request, and restart all server");
            for (RemoteFileServer server : badServers){
                server.setBucketToCopy("");
                server.setStatus(Status.ALL_CRASHED);
            }
            // notify the client that they should send the request again
            return false;
        }

        for(RemoteFileServer badServer: badServers){
            badServer.setBucketToCopy(goodServers.get(0).getBucket());
            badServer.setStatus(Status.CRASHED);
        }
        return true;
    }




    // execute the 2PC algorithm
    @Override
    public Result twoPcSend(Task task) throws RemoteException {
        // phase1: prepare
        List<Future<Result>> futures = new ArrayList<>();

        Utilities.log(this.id, "send " + "preparing" + " requests  to all server");

        for (RemoteFileServer stub: this.serverMap.values()) {
            Future<Result> res = executorService.submit(() -> {
                try {
                    Result result;
                    result = stub.startPrepare(task);
                    return result;

                }catch (Exception e) {
                    return new Result(Status.REJECT, "server bucket: " + stub.getBucket() +
                            " is crashed during prepare");
                }
            });
            futures.add(res);
        }

        int prepareCount = 0;

        Result response;
        for (Future<Result> future: futures) {
            try {
                response = future.get();
                if (Status.OK == response.getStatus()) {
                    prepareCount++;
                }
            } catch (ExecutionException | InterruptedException e) {
                // wait 0.5 second to get the future object
                try {
                    Thread.sleep(500);
                    response = future.get(2000, TimeUnit.MILLISECONDS);
                    if (Status.PREPARED == response.getStatus()) {
                        prepareCount++;
                    }
                } catch (ExecutionException | InterruptedException ex) {
                    return new Result(Status.REJECT, "Error occurred when getting prepare future");
                } catch (TimeoutException ex) {
                    return new Result(Status.REJECT, "Timeout occurred when getting committing future");
                }
            }
        }

        if (prepareCount != this.serverMap.size()){
            return  new Result(Status.REJECT, "Some of servers are failed during the prepare phase");
        }

        futures.clear();


        //phase 2: commit
        int commitCount = 0;
        Result result = null;
        String badMessage = "";

        List<RemoteFileServer> learnedAcceptor = new ArrayList<>();
        List<RemoteFileServer> unlearnedAcceptor = new ArrayList<>();

        for (RemoteFileServer stub: this.serverMap.values()) {
            Future<Result> res = executorService.submit(() -> stub.startCommit(task));


            try {
                result =  res.get(2000, TimeUnit.MILLISECONDS);
                if (result.getStatus() == Status.OK){
                    learnedAcceptor.add(stub);
                    commitCount++;
                }else {
                    unlearnedAcceptor.add(stub);
                }
            } catch (ExecutionException | InterruptedException | TimeoutException e) {
                try {
                    Thread.sleep(500);
                    if (result.getStatus() == Status.OK) {
                        learnedAcceptor.add(stub);
                        commitCount++;
                    }
                }catch (Exception ex){
                    unlearnedAcceptor.add(stub);
                }

            }
        }


        if (commitCount == serverMap.size()){
            Utilities.log(this.id, "All server "  +  " committed successfully");
            return new Result(Status.OK,  "All server committed successfully");
        }else{
            this.recoverMode(unlearnedAcceptor, learnedAcceptor);
            Utilities.log(this.id, "Failed! one or more server refused ");
            Utilities.log(this.id, "start the recovering mode");
            return new Result(Status.REJECT, "Failed during commit" + "Failed! one or more server refuse" +
                    "start the recovering mode");
        }

    }
}
