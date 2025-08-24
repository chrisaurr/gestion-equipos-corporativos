# Changelog

Todos los cambios notables de este proyecto serán documentados en este archivo.

El formato está basado en [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
y este proyecto adhiere a [Versionado Semántico](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Planned for v0.2.0
- **Docker Setup**: Configuración Docker Compose para desarrollo fácil
- **Seeders**: Usuarios por defecto para setup inicial sin configuración manual
- Módulo de gestión de equipos con herencia de tipos
- Sistema de asignaciones de equipos a empleados

### Planned for v0.3.0  
- Módulo de reportes con carga de imágenes
- Configuración de JaCoCo para cobertura de pruebas
- Testing automatizado completo

## [0.1.0] - 2024-08-24

### Added
- **Configuración inicial del proyecto**
  - Configuración Spring Boot 3.5.4 con Java 21
  - Integración PostgreSQL con Flyway para migraciones
  - Configuración Spring Security con autenticación BCrypt
  - Frontend AdminLTE 3.2.0 con Bootstrap 4
  - Configuración QueryDSL para consultas type-safe
  - Integración Lombok y ModelMapper

- **Módulo de Autenticación y Seguridad**
  - Sistema de login con validación BCrypt
  - Gestión de sesiones con Spring Security
  - Roles dinámicos ADMIN/USER basados en BD
  - Servicio de contexto de usuario actual (AuthService)

- **Módulo de Usuarios y Empleados**
  - CRUD completo de usuarios con Ajax y modales Bootstrap
  - Sistema dual: usuarios regulares vs. empleados vinculados
  - Estados de usuario: ACTIVO, INACTIVO, VACACIONES
  - Relación 1:1 opcional usuario-empleado
  - Interfaz Ajax con DataTables para paginación y filtros
  - Soft delete para preservar integridad histórica

- **Módulo de Gestión de Áreas**
  - CRUD de áreas organizacionales
  - Sistema de asignación de responsables (usuarios o empleados)
  - Integración con historial de empleados para ascensos/descensos
  - Campos de auditoría automáticos via sesión

- **Sistema de Historial y Auditoría**
  - Triggers PostgreSQL para historial automático de empleados
  - Tipos de cambio: INGRESO, TRANSFERENCIA, ASCENSO_JEFE, DESCENSO, REINGRESO
  - Timeline visual de cambios de empleados
  - Preservación completa de historial para auditoría

- **Base de Datos**
  - Esquema completo con ENUMs de PostgreSQL
  - Triggers automáticos para historial de empleados
  - Migraciones Flyway: V1 (schema inicial), V2 (historial), V3 (triggers)
  - Esquema preparado para módulos futuros (equipos, reportes, asignaciones)
  - Índices optimizados para rendimiento

- **Frontend Interactivo**
  - Plantillas Thymeleaf con layout común
  - Modales Bootstrap para todas las operaciones CRUD
  - Ajax DataTables con filtros inline y paginación
  - Alertas dinámicas y manejo de errores
  - Timeline visual para historial de empleados
  - Interfaz responsive con AdminLTE

### Technical Decisions
- PostgreSQL como BD principal por soporte avanzado de ENUMs y triggers
- Flyway para control de versiones de BD
- QueryDSL para consultas complejas y filtros dinámicos
- Soft delete para preservar integridad de historial
- Triggers de BD vs. lógica Java para historial automático
- SessionScoped AuthService para contexto de usuario
- DTO pattern para separación de capas

### Database Schema
- `usuario`: Autenticación y datos básicos
- `empleado`: Vinculación opcional 1:1 con usuario
- `area`: Organización departamental con responsables
- `historial_empleado`: Seguimiento automático de cambios
- Schema completo preparado para equipos (vehiculo, electronico, mobiliario, herramienta)
- Schema preparado para reportes e historial de asignaciones

### Security & Authentication
- BCrypt para encriptación de contraseñas
- Spring Security 6 con configuración moderna
- Protección CSRF habilitada
- Validación de roles en controladores
- Contexto de sesión para auditoría

### Development Infrastructure
- Maven con plugins QueryDSL y Lombok
- Profiles para desarrollo y Docker
- Configuración Flyway automática
- Hot reload Thymeleaf en desarrollo

---

## Tipos de Cambios

- `Added` para nuevas funcionalidades
- `Changed` para cambios en funcionalidad existente
- `Deprecated` para funcionalidades que serán removidas
- `Removed` para funcionalidades removidas
- `Fixed` para corrección de bugs
- `Security` para vulnerabilidades corregidas