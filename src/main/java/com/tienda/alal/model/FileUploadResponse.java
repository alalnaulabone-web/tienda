package com.tienda.alal.model;

public class FileUploadResponse {

    private Long fileId;
    private String originalName;
    private String mimeType;
    private Long size;

    public FileUploadResponse() {
    }

    public FileUploadResponse(Long fileId, String originalName, String mimeType, Long size) {
        this.fileId = fileId;
        this.originalName = originalName;
        this.mimeType = mimeType;
        this.size = size;
    }

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }
}
