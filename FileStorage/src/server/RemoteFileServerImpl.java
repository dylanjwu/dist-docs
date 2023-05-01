package server;

import Utilities.Utilities;
import com.example.api.Coordinator;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import com.example.api.RemoteFileServer;
import paxos.*;

import java.io.*;
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
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RemoteFileServerImpl extends UnicastRemoteObject implements RemoteFileServer {
    private AmazonS3 s3Client;
    private String bucket;

    private int cPort;

    private String cHost;

    private int port;
    private String host;
    private Coordinator myCoordinator;

    private Proposal committedProposal = null;

    private Lock lock = new ReentrantLock();

    private long proposalId = 0L;
    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    private Status status;

    private String otherBucket = "";


    protected RemoteFileServerImpl(AmazonS3 s3Client, int port, String cHost, int cPort, String bucketName) throws RemoteException, UnknownHostException {
        super();
        this.s3Client = s3Client;
        this.bucket =  bucketName;
        this.port = port;
        this.cHost = cHost;
        this.cPort = cPort;
        this.status = Status.IDLE;



//        this.host = InetAddress.getByName("localhost").getHostAddress();
//        try {
        this.host = InetAddress.getLocalHost().getHostAddress();
//
//            Registry registry = LocateRegistry.createRegistry(port);
//
//            registry.bind("rmi://" + this.host + ":" + port + "/RemoteFileServer", this);
//            System.out.println("RemoteFileServer is ready and waiting for client connections..., " +
//                    "and bind on the address: " + "rmi://" + host + ":" + port + "/RemoteFileServer" );
//
//
//        } catch (UnknownHostException e) {
//            throw new RuntimeException(e);
//        } catch (AlreadyBoundException e) {
//            throw new RuntimeException(e);
//        }

//        this.setCoordinator(this.cHost, this.cPort);
    }

    // constructor for recovering
    protected RemoteFileServerImpl(AmazonS3 s3Client, int port, String cHost, int cPort, String bucketName, String copyBucket) throws RemoteException, UnknownHostException {
        super();
        this.s3Client = s3Client;
        this.bucket =  bucketName;
        this.port = port;
        this.cHost = cHost;
        this.cPort = cPort;
        this.status = Status.IDLE;

//        this.host = InetAddress.getByName("localhost").getHostAddress();
        this.host = InetAddress.getLocalHost().getHostAddress();
//            Registry registry = LocateRegistry.createRegistry(port);
//
//            registry.bind("rmi://" + this.host + ":" + port + "/RemoteFileServer", this);
//            System.out.println("RemoteFileServer is ready and waiting for client connections..., " +
//                    "and bind on the address: " + "rmi://" + host + ":" + port + "/RemoteFileServer" );
//        this.setCoordinator(this.cHost, this.cPort);
        this.recover(copyBucket);
    }

    // use TwoPC
    public boolean createFile(String fileName, long id, byte[] contents) throws RemoteException {
        String filePath = id + "/" + fileName;

        Task task = new Task(Type.CREATE, filePath, id, contents, Status.IDLE);
        Result  result = this.myCoordinator.twoPcSend(task);

        if (result.getStatus() == Status.OK){
            System.out.println("Successfully create the file in " + filePath );
            return true;
        }else{
            System.out.println("Error occurred please create the file again " + filePath);
            return false;
        }

    }

    // read does not require Paxos or 2PC
    public byte[] readFile(String fileName, long id) throws RemoteException {
        String filePath = id + "/" + fileName;
        S3Object object = s3Client.getObject(new GetObjectRequest(this.bucket, filePath));

        try (InputStream inputStream = object.getObjectContent()) {
            return IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            throw new RemoteException("Error downloading file from S3", e);
        }
    }

    // Order is important in updating, thus using Paxos
    public boolean updateFile(String fileName, byte[] file, long id) throws RemoteException {
        String filePath = id + "/" + fileName;

        if (!s3Client.doesObjectExist(this.bucket, filePath)) {
            return false;
        }

        Result result = this.myCoordinator.execute(new Task(Type.UPDATE, filePath, id, file, Status.IDLE));

        return result.getStatus() == Status.OK;
    }

    public boolean deleteFile(String fileName, long id) throws RemoteException {
        String filePath = id + "/" + fileName;

        Result result = this.myCoordinator.twoPcSend(new Task(Type.DELETE, filePath, id, new byte[0], Status.IDLE));
        return result.getStatus() == Status.OK;
    }

    @Override
    public String getHost() throws RemoteException {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getPort() throws RemoteException {
        return this.port;
    }

    @Override
    public void setCoordinator(String host, int port) throws RemoteException {
        try {



            if (host.equals("localhost")) {
                host = InetAddress.getLocalHost().getHostAddress();
            }else{
                host = InetAddress.getByName(host).getHostAddress();
            }

            Registry registry = LocateRegistry.getRegistry(host, port);
            System.out.println("rmi://" + host + ":" + port + "/Coordinator");
//            this.myCoordinator = (Coordinator) registry.lookup("rmi://" + host + ":" + port + "/Coordinator");

            this.myCoordinator = (Coordinator) Naming.lookup("rmi://" + host + ":" + port + "/Coordinator");

            Utilities.log(bucket, "Server has added a coordinator: "
                    + "rmi://" + host + ":" + port + "/Coordinator");

            this.myCoordinator.addAcceptor(this.host, this.port);

        }catch (RemoteException e){
            throw new RemoteException("Can not find the coordinator");
        } catch (NotBoundException e) {
            throw new RuntimeException("Undefined Error");
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Promise promise(Proposal proposal) throws RemoteException {
        Future<Promise> future = this.executorService.submit(() -> {
            if (proposal.getId() <= this.proposalId) {
                return new Promise(Status.REJECT, proposal);
            }
            this.proposalId = proposal.getId();
            if (this.committedProposal != null){
                return new Promise(Status.ACCEPTED, new Proposal(this.committedProposal.getId(), this.committedProposal.getTask()));
            }else {
                return new Promise(Status.PREPARED, proposal);
            }
        });

        try{
            return future.get(2000, TimeUnit.MILLISECONDS);
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            Utilities.log(this.bucket, "Server is crushed or Timeout during the preparation");
            return new Promise(Status.REJECT, null);
        }
    }

    @Override
    public Boolean accept(Proposal proposal) throws RemoteException {

        Future<Boolean> future = this.executorService.submit(() -> {
            if (proposal.getId() != this.proposalId) {
                return false;
            }

            if (this.committedProposal == null){
                this.committedProposal = proposal;
            }else {
                this.committedProposal.setId(proposal.getId());
                this.committedProposal.setTask(proposal.getTask());
            }
            return true;
        });

        try{
            return future.get(2000, TimeUnit.MILLISECONDS);
        } catch (ExecutionException | InterruptedException e) {
            Utilities.log(this.bucket, "Server is crushed during the preparation");
            return false;
        } catch (TimeoutException e){
            Utilities.log(this.bucket, "Server is timeout during the accept");
            return false;
        }
    }

    @Override
    public Result learn(Proposal proposal) throws RemoteException {
        Task task = proposal.getTask();
        Result result = null;

        try{
            Future<Result> future = this.executorService.submit(() -> {
                if (task.getOperation()  == Type.UPDATE){
                    this.lock.lock();
                    try (InputStream inputStream = new ByteArrayInputStream(task.getContents())) {
                        ObjectMetadata metadata = new ObjectMetadata();
                        metadata.setContentLength(task.getContents().length);
                        s3Client.putObject(new PutObjectRequest(bucket,  task.getFileName(), inputStream, metadata));
                    } catch (IOException e) {
                        return new Result(Status.REJECT, "Error updating file on S3");
                    }finally {
                        this.lock.unlock();
                    }
                    return new Result(Status.OK, "Successfully updated the file");
                }else{
                    return new Result(Status.REJECT, "");
                }
            });

            result = future.get();
            return result;
        } catch (ExecutionException | InterruptedException e) {

            return new Result(Status.REJECT, "Server Crashed during learning");
        }
    }

    @Override
    public void recover(String srcBucket) throws RemoteException {
        System.out.println(" start copying from the bucket: " + srcBucket);
        ObjectListing objectListing = s3Client.listObjects(srcBucket);
        while (true) {
            for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
                String srcKey = objectSummary.getKey();
                CopyObjectRequest copyObjectRequest = new CopyObjectRequest(srcBucket, srcKey, this.bucket, srcKey);
                s3Client.copyObject(copyObjectRequest);
            }

            if (objectListing.isTruncated()) {
                objectListing = s3Client.listNextBatchOfObjects(objectListing);
            } else {
                break;
            }
        }
    }

    @Override
    public String getBucket() throws RemoteException {
        return this.bucket;
    }

    @Override
    public Result startPrepare(Task task) {
        try {
            Result result = new Result(Status.OK, "prepare successfully");

            Utilities.log(this.bucket, "Prepare Successfully");

            return result;
        }catch (Exception e){
            return new Result(Status.REJECT, "Prepared failed");
        }
    }

    @Override
    public Result startCommit(Task task) {
        Utilities.log(this.bucket, "start Committing");
        try {
//            Future<Result> res = executorService.submit(()-> {
            this.lock.lock();
           Type type = task.getOperation();
           if (type.equals(Type.CREATE)) {
//               if (task.getContents().length == 0) {
//                   byte[] emptyContent = task.getContents();
//                   InputStream emptyStream = new ByteArrayInputStream(emptyContent);
//                   ObjectMetadata metadata = new ObjectMetadata();
//                   metadata.setContentLength(0);
//                   String file = task.getFileName();
//                   s3Client.putObject(new PutObjectRequest(this.bucket, file, emptyStream, metadata));
//               }else{
               byte[] contents = task.getContents();
               String file = task.getFileName();
               InputStream contentStream = new ByteArrayInputStream(contents);

               ObjectMetadata metadata = new ObjectMetadata();
               metadata.setContentLength(contents.length);
               s3Client.putObject(new PutObjectRequest(this.bucket, file, contentStream, metadata));
//               }
               Utilities.log(this.bucket, "Commit Successfully");
               return new Result(Status.OK, "Commit successfully");

           } else{

               String filePath = task.getFileName();
               if (!s3Client.doesObjectExist(this.bucket, filePath)) {
                   Utilities.log(this.bucket, "Commit Successfully");
                   return new Result(Status.OK, "File not found");
               }

               s3Client.deleteObject(new DeleteObjectRequest(this.bucket, filePath));
               Utilities.log(this.bucket, "Commit Successfully");
               return new Result(Status.OK, "Commit Successfully");
           }

       }catch (Exception e){
           Utilities.log(this.bucket, "commit failed !");
           return new Result(Status.REJECT, "commit Failed");
       }finally {
           this.lock.unlock();
       }

    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String getBucketToCopy() throws RemoteException {
        return this.otherBucket;
    }

    @Override
    public void setBucketToCopy(String bucket) throws RemoteException {
        this.otherBucket = bucket;
    }
}
