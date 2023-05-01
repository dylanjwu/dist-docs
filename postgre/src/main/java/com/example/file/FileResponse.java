package com.example.file;

public class FileResponse {
    private String message;

    private int status;

    private long id;

    public FileResponse(int status, String message) {
        this.message = message;
        this.status = status;
    }

    public FileResponse(int status, String message, long id) {
        this.message = message;
        this.status = status;
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getStatus() { return status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "FileResponse{" +
                "message='" + message + '\'' +
                ", status=" + status +
                ", id=" + id +
                '}';
    }
}