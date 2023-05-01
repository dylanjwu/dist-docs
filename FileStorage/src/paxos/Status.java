package paxos;
/**

 Enum class representing the status of various operations in the two-phase commit protocol.
 ACCEPTED: The proposal has been accepted by the server.
 PREPARED: The server is prepared to execute the proposal.
 REJECT: The proposal has been rejected by the server.
 OK: The operation has been completed successfully.
 IDLE: The server is currently not processing any requests.
 */
public enum Status {
    ACCEPTED, PREPARED, REJECT, OK, IDLE, CRASHED, ALL_CRASHED

}
