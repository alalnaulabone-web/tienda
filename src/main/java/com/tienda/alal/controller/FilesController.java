package com.tienda.alal.controller;

import com.tienda.alal.exception.FileUploadException;
import com.tienda.alal.model.FileMetadataResponse;
import com.tienda.alal.model.FileUploadResponse;
import com.tienda.alal.service.FileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/files")
public class FilesController {

    private final FileService fileService;

    public FilesController(FileService fileService) {
        this.fileService = fileService;
    }

    /**
     * Endpoint para upload de archivos
     * 
     * POST /files/upload
     * Content-Type: multipart/form-data
     * 
     * Respuesta exitosa:
     * {
     *   "status": 200,
     *   "data": {
     *     "fileId": 15,
     *     "originalName": "documento.pdf",
     *     "mimeType": "application/pdf",
     *     "size": 1024000
     *   }
     * }
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            FileUploadResponse response = fileService.uploadFile(file);
            return ResponseEntity.ok(Map.of(
                    "status", 200,
                    "data", response
            ));
        } catch (FileUploadException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", 400,
                    "error", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", 500,
                    "error", "Error interno del servidor"
            ));
        }
    }

    /**
     * Obtiene la metadata de un archivo
     * 
     * GET /files/{fileId}/metadata
     */
    @GetMapping("/{fileId}/metadata")
    public ResponseEntity<?> getFileMetadata(@PathVariable Long fileId) {
        try {
            FileMetadataResponse response = fileService.getFileMetadata(fileId);
            return ResponseEntity.ok(Map.of(
                    "status", 200,
                    "data", response
            ));
        } catch (FileUploadException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", 404,
                    "error", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", 500,
                    "error", "Error interno del servidor"
            ));
        }
    }

    /**
     * Verifica si un archivo existe
     * 
     * GET /files/{fileId}/exists
     */
    @GetMapping("/{fileId}/exists")
    public ResponseEntity<?> fileExists(@PathVariable Long fileId) {
        try {
            boolean exists = fileService.fileExists(fileId);
            return ResponseEntity.ok(Map.of(
                    "status", 200,
                    "data", Map.of("exists", exists)
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", 500,
                    "error", "Error interno del servidor"
            ));
        }
    }

    /**
     * Elimina un archivo
     * 
     * DELETE /files/{fileId}
     */
    @DeleteMapping("/{fileId}")
    public ResponseEntity<?> deleteFile(@PathVariable Long fileId) {
        try {
            fileService.deleteFile(fileId);
            return ResponseEntity.ok(Map.of(
                    "status", 200,
                    "mensaje", "Archivo eliminado exitosamente"
            ));
        } catch (FileUploadException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", 404,
                    "error", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", 500,
                    "error", "Error interno del servidor"
            ));
        }
    }
}
