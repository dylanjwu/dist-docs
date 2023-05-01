package paxos;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Represents a Proposal object used in the server for handling distributed consensus.
 * The Proposal object contains an ID and a task.
 */
public class Proposal implements Serializable {
    private static final long serialVersionUID = 1L;
    private long id;
    private Task task;

    /**
     * Constructs a new Proposal with the given ID and task.
     *
     * @param id   The ID of the Proposal.
     * @param task The task associated with the Proposal.
     */
    public Proposal(long id, Task task) {
        this.id = id;
        this.task = task;
    }


    /**
     * Retrieves the ID of the Proposal.
     *
     * @return The ID of the Proposal.
     */
    public long getId() {
        return id;
    }


    /**
     * Sets the ID of the Proposal.
     *
     * @param id The new ID for the Proposal.
     */
    public void setId(long id) {
        this.id = id;
    }



    /**
     * Retrieves the task associated with the Proposal.
     *
     * @return The task associated with the Proposal.
     */
    public Task getTask() {
        return task;
    }


    /**
     * Sets the task associated with the Proposal.
     *
     * @param task The new task for the Proposal.
     */
    public void setTask(Task task) {
        this.task = task;
    }


    /**
     * Returns a string representation of the Proposal object.
     *
     * @return A string representation of the Proposal object.
     */
    @Override
    public String toString() {
        return "Proposal{" +
                "id=" + id +
                ", task=" + task +
                '}';
    }


    /**
     * Creates a new Proposal object with a unique ID and the given task.
     * The unique ID is based on the current date and time.
     *
     * @param task The task associated with the new Proposal.
     * @return A new Proposal object with a unique ID.
     */
    public static synchronized Proposal createProposal(Task task){
        String id = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
        Proposal proposal = new Proposal(Long.parseLong(id), task);
        try {
            Thread.sleep(1);
        }catch (InterruptedException e){
            throw new RuntimeException("Failed to create the proposal");
        }
        return proposal;
    }

}
