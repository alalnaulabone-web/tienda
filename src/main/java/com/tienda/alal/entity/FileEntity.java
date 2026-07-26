package com.tienda.alal.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "files")
@Getter
@Setter
public class FileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "original_name", nullable = false)
    private String originalName;

    @Column(name = "internal_name", nullable = false)
    private String internalName;

    @Column(name = "storage_path", nullable = false)
    private String storagePath;

    @Column(name = "mime_type", nullable = false)
    private String mimeType;

    @Column(name = "size", nullable = false)
    private Long size;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public FileEntity() {
    }

    public FileEntity(String originalName, String internalName, String storagePath,
                     String mimeType, Long size, LocalDateTime createdAt) {
        this.originalName = originalName;
        this.internalName = internalName;
        this.storagePath = storagePath;
        this.mimeType = mimeType;
        this.size = size;
        this.createdAt = createdAt;
    }
}
