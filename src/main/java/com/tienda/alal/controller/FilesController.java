package com.tienda.alal.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tienda.alal.model.FileMetadataResponse;
import com.tienda.alal.model.FileUploadResponse;
import com.tienda.alal.service.FileService;

@RestController
@RequestMapping("/files")
public class FilesController {

    private final FileService fileService;

    public FilesController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadFile(@RequestParam("file") MultipartFile file) {
        FileUploadResponse response = fileService.uploadFile(file);
        return ResponseEntity.ok(Map.of("status", 200, "data", response));
    }

    @GetMapping("/{fileId}/metadata")
    public ResponseEntity<Map<String, Object>> getFileMetadata(@PathVariable Long fileId) {
        FileMetadataResponse response = fileService.getFileMetadata(fileId);
        return ResponseEntity.ok(Map.of("status", 200, "data", response));
    }

    @GetMapping("/{fileId}/exists")
    public ResponseEntity<Map<String, Object>> fileExists(@PathVariable Long fileId) {
        boolean exists = fileService.fileExists(fileId);
        return ResponseEntity.ok(Map.of("status", 200, "data", Map.of("exists", exists)));
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<Map<String, Object>> deleteFile(@PathVariable Long fileId) {
        fileService.deleteFile(fileId);
        return ResponseEntity.ok(Map.of("status", 200, "mensaje", "Archivo eliminado exitosamente"));
    }
}
