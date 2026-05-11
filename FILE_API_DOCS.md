# 📤 API de Upload de Archivos - Documentación

## 🎯 Arquitectura del Sistema

La API implementa un sistema desacoplado de manejo de archivos siguiendo principios SOLID y arquitectura limpia:

```
FilesController (REST)
        ↓
    FileService (Lógica de Negocio)
        ↓
    ├─ StorageService (Almacenamiento Físico)
    └─ FileRepository (Persistencia en BD)
```

## 📋 Componentes Creados

### 1. **Entity - FileEntity**
- Mapea la tabla `files` de MySQL
- Almacena metadata del archivo
- Campo `internalName`: UUID para evitar colisiones
- Campo `storagePath`: ruta completa del archivo

### 2. **DTOs**
- **FileUploadResponse**: Respuesta del upload
- **FileMetadataResponse**: Metadata del archivo

### 3. **Validación - FileValidation**
Implementa validaciones de seguridad:
- ✅ Archivos no vacíos
- ✅ Tamaño máximo: 50 MB
- ✅ MIME types permitidos
- ✅ Extensiones válidas
- ✅ Prevención de path traversal
- ✅ Sanitización de nombres

### 4. **Servicios**
- **StorageService**: Almacenamiento físico con UUID
- **FileService**: Lógica de negocio y transacciones

### 5. **Repository - FileRepository**
- JPA Repository para operaciones CRUD

### 6. **Controller - FilesController**
- Endpoints REST para upload, metadata, verificación y eliminación

## 🔗 Endpoints Disponibles

### 📤 1. Upload de Archivo
```
POST /files/upload
Content-Type: multipart/form-data

Parámetro: file (MultipartFile)
```

**Respuesta Exitosa (200):**
```json
{
  "status": 200,
  "data": {
    "fileId": 15,
    "originalName": "documento.pdf",
    "mimeType": "application/pdf",
    "size": 1024000
  }
}
```

**Error de Validación (400):**
```json
{
  "status": 400,
  "error": "El tamaño del archivo excede el límite de 50 MB"
}
```

### 📋 2. Obtener Metadata del Archivo
```
GET /files/{fileId}/metadata
```

**Respuesta (200):**
```json
{
  "status": 200,
  "data": {
    "id": 15,
    "originalName": "documento.pdf",
    "mimeType": "application/pdf",
    "size": 1024000,
    "createdAt": "2026-05-10T14:30:45.123456"
  }
}
```

### ✅ 3. Verificar Existencia de Archivo
```
GET /files/{fileId}/exists
```

**Respuesta (200):**
```json
{
  "status": 200,
  "data": {
    "exists": true
  }
}
```

### 🗑️ 4. Eliminar Archivo
```
DELETE /files/{fileId}
```

**Respuesta (200):**
```json
{
  "status": 200,
  "mensaje": "Archivo eliminado exitosamente"
}
```

## 🛡️ Seguridad Implementada

### Validaciones de Tipo
- MIME types permitidos: `image/jpeg`, `image/png`, `image/gif`, `image/webp`, `application/pdf`, `application/msword`, `application/vnd.openxmlformats-officedocument.wordprocessingml.document`, `application/vnd.ms-excel`, `application/vnd.openxmlformats-officedocument.spreadsheetml.sheet`, `text/plain`, `text/csv`
- Extensiones permitidas: `jpg`, `jpeg`, `png`, `gif`, `webp`, `pdf`, `doc`, `docx`, `xls`, `xlsx`, `txt`, `csv`

### Límites de Tamaño
- Máximo por archivo: 50 MB
- Configurable en `application.properties`: `file.upload-dir`

### Prevención de Ataques
- ✅ UUID para nombres internos (sin colisiones)
- ✅ Path traversal prevention (no permite `..`, `/`, `\`)
- ✅ Sanitización de caracteres especiales
- ✅ Validación de nombres peligrosos

### Gestión de Transacciones
- `@Transactional` en operaciones de BD
- Rollback automático en errores

## 📝 Ejemplo de Uso Completo

### Paso 1: Upload del archivo
```bash
curl -X POST http://localhost:8080/files/upload \
  -F "file=@documento.pdf"
```

Respuesta:
```json
{
  "fileId": 15
}
```

### Paso 2: Usar fileId en otra entidad
```bash
POST /tienda/crear
{
  "nombre": "Tienda Central",
  "comentario": "Sucursal principal",
  "fileId": 15
}
```

### Paso 3: Verificar archivo (opcional)
```bash
GET /files/15/metadata
```

## 🗄️ Base de Datos

### Tabla `files`
```sql
CREATE TABLE files (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    original_name VARCHAR(255) NOT NULL,
    internal_name VARCHAR(255) NOT NULL,
    storage_path VARCHAR(500) NOT NULL,
    mime_type VARCHAR(100) NOT NULL,
    size BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## ⚙️ Configuración

En `application.properties`:
```properties
# Directorio de uploads
file.upload-dir=uploads

# Límites de tamaño
spring.servlet.multipart.max-file-size=50MB
spring.servlet.multipart.max-request-size=50MB
```

## 🏗️ Principios Aplicados

✅ **SOLID**
- Single Responsibility: Cada servicio tiene una responsabilidad
- Dependency Injection: Inyección de dependencias
- Open/Closed: Fácil extensión sin modificación

✅ **Arquitectura Limpia**
- Separación de capas (Controller → Service → Repository)
- DTOs para respuestas
- Excepciones personalizadas

✅ **Seguridad**
- Validaciones exhaustivas
- Prevención de ataques
- Nombres únicos con UUID

✅ **Mantenibilidad**
- Código modular y reutilizable
- Comentarios y documentación
- Fácil de testear

## ⚠️ Restricciones Respetadas

✅ No guarda archivos como BLOB
✅ No modifica endpoints existentes
✅ No crea asociaciones automáticas
✅ No guarda automáticamente fileId en otras entidades
✅ No acopla uploads a módulos específicos
✅ No expone rutas internas reales
✅ No usa nombres originales como nombres físicos

## 🚀 Próximos Pasos

1. Ejecutar el script SQL para crear la tabla
2. Compilar la aplicación: `mvn clean install`
3. Iniciar la aplicación
4. Probar endpoints con Postman o curl
5. Integrar fileId en otros endpoints según sea necesario
