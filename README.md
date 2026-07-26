# API Spring Boot - Tienda

Esta es una API REST desarrollada con Spring Boot que se conecta a una base de datos MySQL para gestionar tiendas.

## 🚀 Inicio Rápido

### Ejecutar la aplicación
```bash
# Ejecutar con Java 21 configurado automáticamente
start.bat
```

### Verificar que funciona
```bash
# Endpoint de prueba
curl http://localhost:8080/test

# Insertar tienda (POST)
curl -X POST http://localhost:8080/tienda \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Mi Tienda",
    "codigo": "T001",
    "id_logo": "logo.png",
    "propietario": "Juan Pérez",
    "cedula": "123456789",
    "ubicacion": "Centro",
    "comentario": "Tienda nueva"
  }'
```

## � Sistema Dinámico de Stored Procedures

La API implementa un **sistema dinámico** que permite ejecutar cualquier stored procedure configurado en la base de datos sin código hardcodeado.

### POST /sp
- **Descripción**: Endpoint genérico para ejecutar cualquier SP configurado
- **Body** (JSON):
  ```json
  {
    "sp_id": 1,
    "parametros": {
      "param1": "valor1",
      "param2": "valor2"
    }
  }
  ```

### Configuración en Base de Datos

**Tabla `stored_procedures`:**
```sql
INSERT INTO stored_procedures (id, nombre, descripcion) 
VALUES (1, 'sp_insertar_tienda', 'Inserta una nueva tienda');
```

**Tabla `sp_validations`:**
```sql
INSERT INTO sp_validations (sp_id, parametro, tipo_validacion, valor_validacion) VALUES
(1, 'nombre', 'REQUIRED', 'true'),
(1, 'propietario', 'REGEX', '^[a-zA-Z\\s]+$'),
(1, 'cedula', 'REGEX', '^[0-9]+$');
```

### Beneficios del Sistema Dinámico
- ✅ **Sin código hardcodeado**: Las validaciones y SP se configuran en BD
- ✅ **Mantenibilidad**: Cambios sin modificar código Java
- ✅ **Flexibilidad**: Nuevos SP sin cambios en la aplicación
- ✅ **Consistencia**: Validaciones centralizadas en base de datos

## �📋 Endpoints

### GET /test
- **Descripción**: Endpoint de prueba básico
- **Respuesta**: `"API funcionando correctamente"`
- **Código HTTP**: `200 OK`

### POST /tienda
- **Descripción**: Inserta una nueva tienda usando el **sistema dinámico** (SP ID: 1)
- **Internamente ejecuta**: `genericService.ejecutarSP(1, params)` con validaciones dinámicas
- **Body** (JSON):
  ```json
  {
    "nombre": "string (obligatorio)",
    "codigo": "string (opcional)",
    "id_logo": "string (opcional)",
    "propietario": "string (obligatorio, sin números)",
    "cedula": "string (obligatorio, solo números)",
    "ubicacion": "string (obligatorio)",
    "comentario": "string (opcional)"
  }
  ```
- **Respuestas**:

#### ✅ Éxito (200 OK)
```json
{
  "status": 200,
  "mensaje": "OK: tienda insertada correctamente"
}
```

#### ❌ Error de validación (400 Bad Request)
```json
{
  "status": 400,
  "mensaje": "Error: [mensaje específico del error]"
}
```
**Posibles errores de validación:**
- "Error: el nombre de la tienda es obligatorio"
- "Error: la ubicación es obligatoria"
- "Error: la cédula es obligatoria"
- "Error: el propietario es obligatorio"
- "Error: el nombre del propietario no debe contener números"
- "Error: la cédula debe contener solo números"

#### 💥 Error del servidor (500 Internal Server Error)
```json
{
  "status": 500,
  "mensaje": "Error interno del servidor"
}
```

## 🛡️ Validaciones Implementadas

La API incluye validaciones tanto del lado del servidor (Java) como potencialmente en la base de datos:

### Validaciones en Java:
- ✅ Campos obligatorios: `nombre`, `ubicacion`, `cedula`, `propietario`
- ✅ Propietario sin números
- ✅ Cédula solo números
- ✅ Limpieza de espacios en blanco

### Manejo de Errores:
- 🔄 **HTTP 200**: Operación exitosa
- 🚫 **HTTP 400**: Error de validación (datos incorrectos)
- 💥 **HTTP 500**: Error interno del servidor

## 🛠 Tecnologías

- **Java**: 21 (Eclipse Adoptium)
- **Spring Boot**: 4.0.5
- **Base de datos**: MySQL 8.0
- **Dependencias principales**:
  - Spring Boot Starter Web
  - Spring Boot Starter Data JDBC
  - MySQL Connector/J
  - Lombok

## ⚙️ Configuración

### Base de datos MySQL
- **Host**: localhost:3306
- **Database**: tienda_db
- **Usuario**: root
- **Contraseña**: 1234

### Stored Procedure requerido
```sql
DELIMITER //
CREATE PROCEDURE sp_insertar_tienda(
    IN p_nombre VARCHAR(255),
    IN p_codigo VARCHAR(50),
    IN p_id_logo VARCHAR(255),
    IN p_propietario VARCHAR(255),
    IN p_cedula VARCHAR(20),
    IN p_ubicacion VARCHAR(255),
    IN p_comentario TEXT
)
BEGIN
    -- Tu lógica para insertar tienda aquí
    INSERT INTO tiendas (nombre, codigo, id_logo, propietario, cedula, ubicacion, comentario, fecha_creacion)
    VALUES (p_nombre, p_codigo, p_id_logo, p_propietario, p_cedula, p_ubicacion, p_comentario, NOW());
END //
DELIMITER ;
```

## ✨ Mejoras Implementadas

### 🔥 De API Básica a Profesional

**Antes:**
```json
// Siempre HTTP 200, incluso con errores
{"mensaje": "Error: ..."}
```

**Ahora:**
```json
// HTTP correcto + JSON estructurado
{
  "status": 400,
  "mensaje": "Error: el nombre del propietario no debe contener números"
}
```

### 🛡️ Validaciones del Lado del Servidor
- Campos obligatorios validados antes de BD
- Reglas de negocio aplicadas en Java
- Mensajes de error específicos y claros
- Protección contra datos inválidos

### 📊 Códigos HTTP Profesionales
- **200 OK**: Operación exitosa
- **400 Bad Request**: Error de validación
- **500 Internal Server Error**: Error del servidor

### 🔧 Arquitectura Mejorada
- `ApiResponse`: Clase para respuestas consistentes
- `ResponseEntity`: Control total sobre HTTP
- Manejo de excepciones robusto
- Separación clara de responsabilidades

## 🔧 Solución de Problemas

### Error "Console.istty()"
- Asegúrate de usar Java 21 (Eclipse Adoptium), no el JDK de Pleiades
- Ejecuta con `start.bat` que configura automáticamente Java 21

### Puerto 8080 ocupado
```bash
# Ver qué proceso usa el puerto
netstat -ano | findstr :8080

# Matar el proceso (reemplaza PID)
taskkill /PID <PID> /F
```

### Problemas de conexión MySQL
- Verifica que MySQL esté corriendo
- Confirma las credenciales en `application.properties`
- Asegúrate de que la base de datos `tienda_db` existe

## 📁 Estructura del Proyecto

```
src/
├── main/
│   ├── java/com/tienda/alal/
│   │   ├── AlalApplication.java          # Clase principal
│   │   ├── controller/
│   │   │   ├── TestController.java       # Endpoint de prueba
│   │   │   └── TiendaController.java     # Endpoint de tienda (con HTTP profesional)
│   │   ├── model/
│   │   │   └── ApiResponse.java          # ✅ NUEVO: Respuesta HTTP estandarizada
│   │   └── service/
│   │       └── TiendaService.java        # ✅ MEJORADO: Validaciones y manejo de errores
│   └── resources/
│       ├── application.properties        # Configuración por defecto
│       └── application-mysql.properties  # Configuración MySQL
└── test/
    └── java/com/tienda/alal/
        └── AlalApplicationTests.java     # Tests unitarios
```