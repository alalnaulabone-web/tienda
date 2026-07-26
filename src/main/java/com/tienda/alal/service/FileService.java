package com.tienda.alal.service;

import java.time.LocalDateTime;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.tienda.alal.entity.FileEntity;
import com.tienda.alal.exception.FileNotFoundException;
import com.tienda.alal.exception.FileUploadException;
import com.tienda.alal.model.FileMetadataResponse;
import com.tienda.alal.model.FileUploadResponse;
import com.tienda.alal.repository.FileRepository;
import com.tienda.alal.validation.FileValidation;

@Service
public class FileService {

    private final FileRepository fileRepository;
    private final StorageService storageService;

    public FileService(FileRepository fileRepository, StorageService storageService) {
        this.fileRepository = fileRepository;
        this.storageService = storageService;
    }

    @Transactional
    public FileUploadResponse uploadFile(MultipartFile file) {
        try {
            FileValidation.validateAll(file);

            String internalName = storageService.storeFile(file);
            String storagePath = storageService.getStoragePath(internalName);

            FileEntity fileEntity = new FileEntity(
                    file.getOriginalFilename(),
                    internalName,
                    storagePath,
                    file.getContentType(),
                    file.getSize(),
                    LocalDateTime.now()
            );

            FileEntity savedFile = fileRepository.save(fileEntity);

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

    public FileMetadataResponse getFileMetadata(Long fileId) {
        FileEntity file = fileRepository.findById(fileId)
                .orElseThrow(() -> new FileNotFoundException("Archivo no encontrado con ID: " + fileId));

        return new FileMetadataResponse(
                file.getId(),
                file.getOriginalName(),
                file.getMimeType(),
                file.getSize(),
                file.getCreatedAt()
        );
    }

    @Transactional
    public void deleteFile(Long fileId) {
        FileEntity file = fileRepository.findById(fileId)
                .orElseThrow(() -> new FileNotFoundException("Archivo no encontrado con ID: " + fileId));

        storageService.deleteFile(file.getInternalName());
        fileRepository.deleteById(fileId);
    }

    public Resource getFileResource(Long fileId) {
        FileEntity file = fileRepository.findById(fileId)
                .orElseThrow(() -> new FileNotFoundException("Archivo no encontrado con ID: " + fileId));
        return storageService.loadFile(file.getInternalName());
    }

    public boolean fileExists(Long fileId) {
        return fileRepository.existsById(fileId);
    }
}
