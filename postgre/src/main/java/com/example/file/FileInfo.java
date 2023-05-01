package com.example.file;

import java.io.Serializable;

public class FileInfo implements Serializable {
    private Long id;
    private String fileName;
    private String contents;

    public FileInfo() {
    }

    public FileInfo(Long id, String fileName, String contents) {
        this.id = id;
        this.fileName = fileName;
        this.contents = contents;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }


}
