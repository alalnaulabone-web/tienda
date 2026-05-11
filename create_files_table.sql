-- SQL para crear la tabla de archivos
-- Ejecuta este script en MySQL antes de iniciar la aplicación

CREATE TABLE IF NOT EXISTS files (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    original_name VARCHAR(255) NOT NULL COMMENT 'Nombre original del archivo subido por el cliente',
    internal_name VARCHAR(255) NOT NULL COMMENT 'Nombre único generado con UUID para almacenamiento',
    storage_path VARCHAR(500) NOT NULL COMMENT 'Ruta completa del archivo en el servidor',
    mime_type VARCHAR(100) NOT NULL COMMENT 'Tipo MIME del archivo (application/pdf, image/jpeg, etc.)',
    size BIGINT NOT NULL COMMENT 'Tamaño del archivo en bytes',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Fecha y hora de creación del registro',
    INDEX idx_created_at (created_at),
    INDEX idx_internal_name (internal_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Tabla para almacenar metadata de archivos';
