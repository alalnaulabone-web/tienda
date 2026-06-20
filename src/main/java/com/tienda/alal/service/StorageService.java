package com.tienda.alal.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.tienda.alal.exception.FileUploadException;

@Service
public class StorageService {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    /**
     * Almacena un archivo en el sistema de archivos
     * 
     * @param file Archivo a almacenar
     * @return Nombre interno generado (UUID)
     */
    public String storeFile(MultipartFile file) {
        try {
            // Crear directorio si no existe
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generar nombre único usando UUID
            String internalName = generateInternalFileName(file.getOriginalFilename());
            
            // Resolver ruta completa
            Path filePath = uploadPath.resolve(internalName);

            // Validar que el archivo no exista (prevención de overwrite)
            if (Files.exists(filePath)) {
                throw new FileUploadException("El archivo ya existe en el sistema");
            }

            // Guardar archivo
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Retornar nombre interno (sin ruta absoluta)
            return internalName;

        } catch (IOException e) {
            throw new FileUploadException("Error al guardar el archivo: " + e.getMessage(), e);
        }
    }

    /**
     * Genera un nombre único para el archivo usando UUID
     * Mantiene la extensión original
     */
    private String generateInternalFileName(String originalFileName) {
        if (originalFileName == null) {
            throw new FileUploadException("El nombre del archivo es nulo");
        }

        String extension = getFileExtension(originalFileName);
        String uuid = UUID.randomUUID().toString();
        
        return extension.isEmpty() ? uuid : uuid + "." + extension;
    }

    /**
     * Extrae la extensión del archivo
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

    /**
     * Elimina un archivo del sistema de archivos
     */
    public void deleteFile(String internalName) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(internalName);
            
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
        } catch (IOException e) {
            throw new FileUploadException("Error al eliminar el archivo: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene la ruta completa de almacenamiento de un archivo
     */
    public String getStoragePath(String internalName) {
        return Paths.get(uploadDir).resolve(internalName).toString();
    }
}
