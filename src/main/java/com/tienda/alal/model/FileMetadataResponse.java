package com.tienda.alal.model;

import java.time.LocalDateTime;

public class FileMetadataResponse {

    private Long id;
    private String originalName;
    private String mimeType;
    private Long size;
    private LocalDateTime createdAt;

    public FileMetadataResponse() {
    }

    public FileMetadataResponse(Long id, String originalName, String mimeType, 
                               Long size, LocalDateTime createdAt) {
        this.id = id;
        this.originalName = originalName;
        this.mimeType = mimeType;
        this.size = size;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
