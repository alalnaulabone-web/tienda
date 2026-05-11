package com.tienda.alal.service;

import com.tienda.alal.entity.FileEntity;
import com.tienda.alal.exception.FileUploadException;
import com.tienda.alal.model.FileMetadataResponse;
import com.tienda.alal.model.FileUploadResponse;
import com.tienda.alal.repository.FileRepository;
import com.tienda.alal.validation.FileValidation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class FileService {

    private final FileRepository fileRepository;
    private final StorageService storageService;

    public FileService(FileRepository fileRepository, StorageService storageService) {
        this.fileRepository = fileRepository;
        this.storageService = storageService;
    }

    /**
     * Procesa y almacena un archivo
     * 
     * @param file Archivo a procesar
     * @return DTO con información del archivo almacenado
     */
    @Transactional
    public FileUploadResponse uploadFile(MultipartFile file) {
        try {
            // Validaciones de seguridad
            FileValidation.validateAll(file);

            // Almacenar archivo en el sistema de archivos
            String internalName = storageService.storeFile(file);
            String storagePath = storageService.getStoragePath(internalName);

            // Crear entidad
            FileEntity fileEntity = new FileEntity(
                    file.getOriginalFilename(),
                    internalName,
                    storagePath,
                    file.getContentType(),
                    file.getSize(),
                    LocalDateTime.now()
            );

            // Guardar en base de datos
            FileEntity savedFile = fileRepository.save(fileEntity);

            // Retornar respuesta DTO
            return new FileUploadResponse(
                    savedFile.getId(),
                    savedFile.getOriginalName(),
                    savedFile.getMimeType(),
                    savedFile.getSize()
            );

        } catch (FileUploadException e) {
            throw e;
        } catch (Exception e) {
            throw new FileUploadException("Error inesperado al procesar el archivo: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene la metadata de un archivo
     * 
     * @param fileId ID del archivo
     * @return Metadata del archivo
     */
    public FileMetadataResponse getFileMetadata(Long fileId) {
        FileEntity file = fileRepository.findById(fileId)
                .orElseThrow(() -> new FileUploadException("Archivo no encontrado con ID: " + fileId));

        return new FileMetadataResponse(
                file.getId(),
                file.getOriginalName(),
                file.getMimeType(),
                file.getSize(),
                file.getCreatedAt()
        );
    }

    /**
     * Elimina un archivo del sistema
     * 
     * @param fileId ID del archivo a eliminar
     */
    @Transactional
    public void deleteFile(Long fileId) {
        FileEntity file = fileRepository.findById(fileId)
                .orElseThrow(() -> new FileUploadException("Archivo no encontrado con ID: " + fileId));

        // Eliminar del sistema de archivos
        storageService.deleteFile(file.getInternalName());

        // Eliminar de la base de datos
        fileRepository.deleteById(fileId);
    }

    /**
     * Verifica si un archivo existe
     * 
     * @param fileId ID del archivo
     * @return true si existe, false caso contrario
     */
    public boolean fileExists(Long fileId) {
        return fileRepository.existsById(fileId);
    }
}
