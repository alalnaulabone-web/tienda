package com.tienda.alal.validation;

import com.tienda.alal.exception.FileUploadException;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.regex.Pattern;

public class FileValidation {

    // Configuración de validación
    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024; // 50 MB
    private static final Set<String> ALLOWED_MIME_TYPES = new HashSet<>(Arrays.asList(
            "image/jpeg",
            "image/png",
            "image/gif",
            "image/webp",
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "text/plain",
            "text/csv"
    ));

    private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>(Arrays.asList(
            "jpg", "jpeg", "png", "gif", "webp",
            "pdf", "doc", "docx", "xls", "xlsx",
            "txt", "csv"
    ));

    private static final String DANGEROUS_PATTERN = "^[a-zA-Z0-9._-]+$";

    /**
     * Valida que el archivo no sea nulo y no esté vacío
     */
    public static void validateFileNotEmpty(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new FileUploadException("El archivo no puede estar vacío");
        }
    }

    /**
     * Valida el tamaño máximo del archivo
     */
    public static void validateFileSize(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new FileUploadException("El tamaño del archivo excede el límite de " + 
                    (MAX_FILE_SIZE / 1024 / 1024) + " MB");
        }
    }

    /**
     * Valida el MIME type del archivo
     */
    public static void validateMimeType(MultipartFile file) {
        String mimeType = file.getContentType();
        if (mimeType == null || !ALLOWED_MIME_TYPES.contains(mimeType)) {
            throw new FileUploadException("Tipo de archivo no permitido: " + mimeType);
        }
    }

    /**
     * Valida la extensión del archivo
     */
    public static void validateFileExtension(MultipartFile file) {
        String originalName = file.getOriginalFilename();
        if (originalName == null || !originalName.contains(".")) {
            throw new FileUploadException("El archivo debe tener una extensión válida");
        }

        String extension = getFileExtension(originalName).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new FileUploadException("Extensión de archivo no permitida: " + extension);
        }
    }

    /**
     * Valida que el nombre del archivo sea seguro
     * Previene path traversal y caracteres peligrosos
     */
    public static void validateFileName(MultipartFile file) {
        String originalName = file.getOriginalFilename();
        if (originalName == null) {
            throw new FileUploadException("El nombre del archivo es inválido");
        }

        // Prevenir path traversal
        if (originalName.contains("..") || originalName.contains("/") || 
            originalName.contains("\\") || originalName.startsWith(".")) {
            throw new FileUploadException("Nombre de archivo sospechoso: contiene caracteres peligrosos");
        }

        // Validar caracteres permitidos
        String fileName = originalName.substring(0, originalName.lastIndexOf("."));
        if (!Pattern.matches(DANGEROUS_PATTERN, fileName)) {
            throw new FileUploadException("Nombre de archivo contiene caracteres no permitidos");
        }
    }

    /**
     * Ejecuta todas las validaciones
     */
    public static void validateAll(MultipartFile file) {
        validateFileNotEmpty(file);
        validateFileSize(file);
        validateMimeType(file);
        validateFileExtension(file);
        validateFileName(file);
    }

    /**
     * Extrae la extensión de un archivo
     */
    public static String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    /**
     * Obtiene el tamaño máximo permitido en MB
     */
    public static long getMaxFileSizeInMB() {
        return MAX_FILE_SIZE / 1024 / 1024;
    }
}
