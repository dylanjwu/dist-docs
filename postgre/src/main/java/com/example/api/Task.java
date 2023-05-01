package com.example.api;

import java.io.Serializable;
import java.util.Arrays;

/**
 Class representing the task to be executed by the coordinator.

 It contains the type of operation, key and value.

 It also stores the status of the task.
 */
public class Task implements Serializable {

    private static final long serialVersionUID = 1L;
    private Type operation;
    private String fileName;
    private long userId;

    private byte[] contents;

    private Status status;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Task(Type operation, String fileName, long userId, byte[] contents, Status status) {
        this.operation = operation;
        this.fileName = fileName;
        this.userId = userId;
        this.contents = contents;
        this.status = status;
    }

    public Type getOperation() {
        return operation;
    }

    public void setOperation(Type operation) {
        this.operation = operation;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public byte[] getContents() {
        return contents;
    }

    public void setContents(byte[] contents) {
        this.contents = contents;
    }

    @Override
    public String toString() {
        return "Task{" +
                "operation=" + operation +
                ", fileName='" + fileName + '\'' +
                ", userId=" + userId +
                ", contents=" + Arrays.toString(contents) +
                ", status=" + status +
                '}';
    }
}
