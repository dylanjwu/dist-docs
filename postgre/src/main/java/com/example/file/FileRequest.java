package com.example.file;

import java.util.List;

public class FileRequest {
    private String filename;
    private List<Long> sharedList;
    private String contents;
    private Long fileId ;
    private Long createBy;

    public FileRequest(){

    }

    public FileRequest(String filename, List<Long> sharedList) {
        this.filename = filename;
        this.sharedList = sharedList;
        this.contents = "";
    }

    public FileRequest(Long id, String contents) {
        this.fileId = id;
        this.contents = contents;
    }

    public List<Long> getSharedList() {
        return sharedList;
    }

    public void setUserid(List<Long> sharedList) {
        this.sharedList = sharedList;
    }

    public FileRequest(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }
    public Long getCreateBy() {
        return createBy;
    }


    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setSharedList(List<Long> sharedList) {
        this.sharedList = sharedList;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public void setCreateBy(Long createBy) {
        this.createBy = createBy;
    }

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }
}