package com.example.user;

public record UserResponse(int status, String cookie) {
    public UserResponse(int status) {
        this(status, null);
    }
    public UserResponse setStatus(int value) {
        return new UserResponse(value, cookie);
    }

    public int getStatus() { return status;
    }
}