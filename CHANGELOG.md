# Changelog

Todos los cambios notables de este proyecto serán documentados en este archivo.

El formato está basado en [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
y este proyecto adhiere a [Versionado Semántico](https://semver.org/spec/v2.0.0.html).

## [Unreleased] - Próximas Funcionalidades

### Planned for v1.1.0
- **SweetAlert2 Integration**: Toast notifications y modales de confirmación elegantes
- **Role-Based Access Control**: Permisos granulares más allá del boolean `is_admin`
- **Docker Compose**: Containerización completa para despliegue DigitalOcean

### Planned for v1.2.0  
- **Testing & QA**: Cobertura JaCoCo 80% en servicios críticos
- **Upload Imágenes**: Múltiples imágenes por reporte
- **Dashboard**: Métricas y analytics del sistema

## [1.0.0] - 2025-09-01

### ✨ Added - Sistema Completo Implementado

**LANZAMIENTO MAYOR**: Sistema de gestión de equipos corporativos completamente funcional desde v0.1.0 → v1.0.0

#### 🔐 Autenticación y Seguridad
- **Autenticación BCrypt**: Sistema de login con usuarios de BD y contraseñas encriptadas
- **Spring Security 6**: Configuración completa con gestión de sesiones
- **Sistema de Roles**: Diferenciación ADMIN (`is_admin=true`) vs usuarios regulares
- **AuthService**: Contexto de usuario para auditoría automática en toda la aplicación

#### 📦 Módulos Principales Implementados (7 módulos funcionales)

**1. Gestión de Usuarios y Empleados**
- CRUD completo con tablas Ajax dinámicas y filtros en tiempo real
- Sistema dual: Usuario puede existir solo o con registro empleado (1:1 opcional)
- Estados: ACTIVO, INACTIVO, VACACIONES, LICENCIA
- Historial automático vía triggers PostgreSQL
- Modales Bootstrap para operaciones CRUD

**2. Gestión de Áreas Organizacionales**
- CRUD de áreas con sistema de responsables
- Usuarios (con/sin empleado) pueden ser jefes de área
- Auditoría automática con session context
- Soft delete preservando integridad histórica

**3. Control de Inventario de Equipos**
- 4 tipos de equipos: Electrónicos, Vehículos, Herramientas, Mobiliario
- Herencia single table con campos específicos por tipo
- Identificadores automáticos: EQ-2025-00001 (triggers PostgreSQL)
- Estados: ACTIVO, INACTIVO, MANTENIMIENTO, SUSPENDIDO, BAJA
- QueryDSL filtering por tipo, estado, ubicación, marca

**4. Sistema de Asignaciones de Equipos**
- Asignación múltiple de equipos a usuarios
- Control de disponibilidad (solo equipos ACTIVOS sin reportes)
- Historial automático de entregas/devoluciones vía triggers
- Estados de devolución: PENDIENTE, CONFIRMADA
- Validaciones de negocio para integridad

**5. Sistema de Reportes con Flujo de Estados**
- Reportes de incidencias con flujo: ABIERTO → EN_PROCESO → RESUELTO/CERRADO
- Sistema de prioridades: BAJA, MEDIA, ALTA, CRÍTICA por causa
- Restricción: Un equipo máximo puede tener un reporte activo
- Bloqueo de asignaciones si equipo tiene reportes ABIERTO/EN_PROCESO
- Validaciones de transición de estados

**6. Catálogos de Soporte**
- Gestión de Marcas: CRUD con filtros para organización de equipos
- Gestión de Ubicaciones: Sistema de ubicaciones físicas
- Gestión de Causas: Catálogo para reportes con prioridades

**7. Sistema de Historial y Auditoría**
- Triggers PostgreSQL automáticos para empleados: TRANSFERENCIA, ASCENSO_JEFE, DESCENSO, REINGRESO
- Timeline visual de cambios en modales
- Solo empleados (no usuarios regulares) generan historial
- Preservación completa para auditoría

#### 🛠️ Arquitectura Técnica Completa
- **Spring Boot 3.5.4** con Java 21
- **PostgreSQL 15+** con Flyway migrations automáticas (V1-V6)
- **QueryDSL** para consultas type-safe y filtrado dinámico
- **Thymeleaf + AdminLTE 3.2.0** frontend responsivo
- **Bootstrap 4** + jQuery + Ajax para UX moderna
- **Lombok** reducción boilerplate

#### 🎨 Patrones de Interfaz Consistentes
- **Tablas Ajax**: Filtros inline, paginación servidor, búsqueda tiempo real
- **Modales vs Páginas**: Modales para CRUD simple, páginas para flujos complejos
- **Botones Contextuales**: Ver (azul), Editar (amarillo), Historial (verde), Eliminar (rojo)
- **Alerts Dinámicos**: Confirmaciones y errores con feedback visual

#### 💾 Base de Datos PostgreSQL Avanzada
- **ENUMs Nativos**: EstadoUsuario, EstadoEquipo, EstadoReporte, TipoCambioEmpleado, etc.
- **6 Triggers Automáticos**: Identificadores, historial empleados, asignaciones
- **Soft Delete**: Integridad referencial preservada
- **Audit Fields**: Created_by, fecha_commit automáticos

### 📚 Documentación Actualizada
- **README.md**: Documentación completa para testers (estado real del sistema)
- **CLAUDE.md**: Guía técnica con todos los módulos implementados
- **CHANGELOG.md**: Historial completo de cambios

### 🔄 Changed - Actualizaciones de Versión
- **Version**: v0.1.0 → v1.0.0 (sistema completo funcional)
- **Package Structure**: 7 módulos con arquitectura completa
- **Database Schema**: Todas las tablas y relaciones implementadas

### 🎯 Sistema Listo para Testing
- **7 Módulos Funcionales**: Usuarios, Áreas, Equipos, Asignaciones, Reportes, Marcas, Ubicaciones
- **Flujos de Negocio**: Validaciones y transiciones de estado implementadas
- **Login**: admin / admin (credenciales reales)
- **UI Consistente**: Patrones Ajax + Modal/Página según complejidad

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