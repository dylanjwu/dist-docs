package com.example.user;

import com.example.entity.File;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UserInfo {

    private Long id;

    private String username;

    private String password;

    private LocalDateTime created_at;


    private Map<String, String> sharedFile;

    public UserInfo() {
    }

    public UserInfo(Long id, String username, String password, LocalDateTime created_at, Map<String, String> sharedFile) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.created_at = created_at;
        this.sharedFile = sharedFile;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public Map<String, String> getSharedFile() {
        return sharedFile;
    }

    public void setSharedFile(Map<String, String> sharedFile) {
        this.sharedFile = sharedFile;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", created_at=" + created_at +
                ", sharedFile=" + sharedFile +
                '}';
    }
}
