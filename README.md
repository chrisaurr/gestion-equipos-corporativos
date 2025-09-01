# Sistema de Gestión Integral de Equipos Corporativos

![Version](https://img.shields.io/badge/version-1.0.0-blue.svg)
![Java](https://img.shields.io/badge/Java-21-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.4-brightgreen.svg)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15+-blue.svg)

Sistema web completo de gestión de inventario corporativo desarrollado con Spring Boot y AdminLTE 3. **Sistema 100% funcional** listo para testing exhaustivo.

## 📋 Estado Actual del Proyecto

**SISTEMA COMPLETO IMPLEMENTADO** - Todas las funcionalidades principales están operativas:

- ✅ **Autenticación BCrypt**: Login seguro con usuarios de base de datos
- ✅ **Gestión de Usuarios/Empleados**: CRUD completo con historial automático
- ✅ **Gestión de Áreas**: Organización departamental con responsables  
- ✅ **Control de Inventario**: Gestión completa de equipos con 4 tipos (electrónicos, vehículos, herramientas, mobiliario)
- ✅ **Sistema de Asignaciones**: Asignación de equipos a empleados con historial automático
- ✅ **Sistema de Reportes**: Reportes de incidencias con flujo de estados y prioridades
- ✅ **Gestión de Marcas y Ubicaciones**: Catálogos para organización de equipos

## 🚀 Inicio Rápido para Testing

### Prerrequisitos

- **Java 21** o superior
- **Maven 3.9+**  
- **PostgreSQL 15+**
- **Git**

### Configuración Rápida

1. **Crear la base de datos**
   ```bash
   # Conectar a PostgreSQL (ajustar según tu configuración)
   psql -U postgres
   
   # Crear la base de datos
   CREATE DATABASE inventario;
   ```

2. **Configurar PostgreSQL** 
   
   Ajustar en `application.properties` si es necesario:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/inventario
   spring.datasource.username=postgres
   spring.datasource.password=1234
   ```

3. **Ejecutar la aplicación**
   ```bash
   mvn clean spring-boot:run
   ```
   
   **Flyway ejecutará automáticamente todas las migraciones y creará datos de ejemplo.**

4. **Acceder a la aplicación**
   ```
   URL: http://localhost:8080
   Credenciales: admin / admin
   ```

### 🔐 Usuarios de Prueba

**La aplicación incluye usuarios de prueba creados automáticamente:**

- **Administrador**: `admin` / `admin` (acceso completo)
- **Usuario regular**: Creados via interfaz web con permisos limitados

**Nota**: El sistema diferencia entre administradores (`is_admin = true`) y usuarios regulares (`is_admin = false`).

## 🛠️ Stack Tecnológico

### Backend
- **Spring Boot 3.5.4** - Framework principal
- **Spring Security** - Autenticación BCrypt
- **Spring Data JPA** - Persistencia con QueryDSL
- **PostgreSQL** - Base de datos principal
- **Flyway** - Migraciones automáticas
- **Lombok** - Reducción de código boilerplate

### Frontend
- **Thymeleaf** - Motor de plantillas
- **AdminLTE 3.2.0** - Framework UI
- **Bootstrap 4** - Framework CSS
- **jQuery + DataTables** - Tablas Ajax dinámicas

### Herramientas
- **Maven** - Gestión de dependencias
- **QueryDSL** - Consultas type-safe
- **ModelMapper** - Mapeo DTO

## 📁 Estructura del Proyecto

```
src/main/java/com/gestion/equipos/
├── config/              # Configuraciones (Security, Database)
├── controller/
│   ├── api/v1/         # Controladores REST API
│   └── web/            # Controladores MVC
├── dto/                # Data Transfer Objects
├── entity/             # Entidades JPA
├── repository/         # Repositorios QueryDSL
├── service/            # Lógica de negocio
└── util/               # Utilidades

src/main/resources/
├── db/migration/       # Scripts Flyway (V1, V2, V3)
├── templates/          # Plantillas Thymeleaf
└── application*.properties
```

## 🗃️ Esquema de Base de Datos

### Tablas Principales (Implementadas)

```sql
-- Autenticación
usuario (id, codigo, usuario, password, primer_nombre, primer_apellido, estado, is_admin)
empleado (id, id_usuario, id_area, cargo, observaciones)

-- Organización
area (id, nombre, id_usuario, activo, creado_por)

-- Historial (Triggers automáticos)
historial_empleado (id, id_empleado, tipo_cambio, motivo, fecha_cambio)
```

### ENUMs PostgreSQL

```sql
CREATE TYPE estado_usuario AS ENUM ('ACTIVO', 'INACTIVO', 'VACACIONES');
CREATE TYPE tipo_cambio_empleado AS ENUM ('INGRESO', 'CAMBIO_CARGO', 'TRANSFERENCIA', 'ASCENSO_JEFE', 'DESCENSO', 'REINGRESO', 'SALIDA');
```

### Esquema Completo (Preparado para Equipos)

Las migraciones incluyen el esquema completo para equipos, reportes y historial de asignaciones, listo para implementación futura.

## ✅ Módulos Implementados y Funcionales

### 🔐 1. Autenticación y Seguridad
- [x] **Login BCrypt**: Sistema de autenticación con usuarios de base de datos
- [x] **Gestión de Sesiones**: Spring Security con control de acceso
- [x] **Roles Administrativos**: Diferenciación ADMIN/USER con campo `is_admin`

### 👥 2. Gestión de Usuarios y Empleados
- [x] **CRUD Completo**: Crear, editar, ver, eliminar usuarios (tabla Ajax con filtros)
- [x] **Sistema Dual**: Usuario puede existir solo o con registro de empleado (1:1 opcional)
- [x] **Estados de Usuario**: ACTIVO, INACTIVO, VACACIONES, LICENCIA
- [x] **Historial Automático**: Triggers PostgreSQL registran cambios automáticamente
- [x] **Interfaz Dinámica**: Modales Bootstrap, paginación, búsqueda en tiempo real

### 🏢 3. Gestión de Áreas
- [x] **CRUD de Áreas**: Creación y gestión de departamentos/áreas
- [x] **Sistema de Responsables**: Asignación de usuarios como jefes de área
- [x] **Auditoría**: Registro automático de quien crea/modifica áreas
- [x] **Soft Delete**: Eliminación lógica preservando historial

### 📦 4. Control de Inventario de Equipos
- [x] **4 Tipos de Equipos**: Electrónicos, Vehículos, Herramientas, Mobiliario
- [x] **Identificadores Automáticos**: EQ-2025-00001 generado por triggers PostgreSQL
- [x] **Estados de Equipos**: ACTIVO, INACTIVO, MANTENIMIENTO, SUSPENDIDO
- [x] **Gestión por Tipo**: Interfaces especializadas para cada tipo de equipo
- [x] **Campos Específicos**: Atributos particulares por tipo (placa, modelo, etc.)

### 📋 5. Sistema de Asignaciones
- [x] **Asignación de Equipos**: Asignar equipos a empleados
- [x] **Control de Disponibilidad**: Verificar equipos disponibles para asignación
- [x] **Historial Automático**: Triggers registran entregas y devoluciones
- [x] **Estados de Devolución**: ENTREGADO, DEVUELTO, PERDIDO, DAÑADO

### 📊 6. Sistema de Reportes
- [x] **Reportes de Incidencias**: Registro de problemas con equipos
- [x] **Flujo de Estados**: ABIERTO → EN_PROCESO → RESUELTO/CERRADO
- [x] **Sistema de Prioridades**: BAJA, MEDIA, ALTA, CRÍTICA (por causa)
- [x] **Validación de Negocio**: Un equipo solo puede tener un reporte activo
- [x] **Interfaz Completa**: Ajax, filtros, paginación, botones contextuales

### 🏷️ 7. Catálogos de Soporte
- [x] **Gestión de Marcas**: CRUD completo con filtros para organizar equipos
- [x] **Gestión de Ubicaciones**: Sistema de ubicaciones para equipos
- [x] **Gestión de Causas**: Catálogo de causas para reportes con prioridades

## 🎯 Funcionalidades de Testing Críticas

### Flujos Principales para QA
1. **Login/Logout** → Autenticación con admin/admin
2. **Usuarios**: Crear → Editar → Crear Empleado → Ver Historial → Eliminar  
3. **Áreas**: Crear → Asignar Responsable → Cambiar Responsable → Ver Historial
4. **Equipos**: Crear por Tipo → Editar → Asignar a Empleado → Devolver
5. **Reportes**: Crear → Cambiar Estado → Validar que bloquea nueva asignación
6. **Catálogos**: Crear Marcas/Ubicaciones → Usar en Equipos

### Validaciones de Negocio Implementadas
- **Un equipo activo**: Solo puede tener un reporte ABIERTO/EN_PROCESO
- **Equipos con reportes**: No se pueden asignar hasta resolver el reporte
- **Transiciones de estado**: Validadas tanto en frontend como backend
- **Empleados vs Usuarios**: Solo empleados generan historial de cambios
- **Soft Delete**: Preserva integridad referencial del historial

## 🧪 Testing y QA

### Para Contribuidores QA

Este proyecto está **específicamente diseñado para práctica de QA**. Áreas de enfoque:

#### 🔍 Áreas Críticas de Testing
- **Autenticación**: Flujos de login/logout, roles, sesiones
- **CRUD Operations**: Crear, editar, eliminar usuarios/áreas
- **Historial**: Verificar triggers automáticos funcionan
- **Validaciones**: Formularios, campos requeridos, formatos
- **Ajax/Modales**: Funcionalidad de interfaz dinámica
- **Soft Delete**: Preservación de datos históricos

#### 🛠️ Comandos de Testing

```bash
# Ejecutar todas las pruebas
mvn test

# Generar reporte de cobertura (cuando esté configurado)
mvn jacoco:report

# Limpiar y reconstruir
mvn clean package
```

#### 🐛 Casos de Prueba Sugeridos

1. **Seguridad**
   - Acceso sin autenticación
   - Escalación de privilegios
   - Inyección SQL en formularios

2. **Funcionalidad**
   - Creación de usuarios duplicados
   - Eliminación de áreas con empleados asignados
   - Cambios de estado de empleados

3. **Interfaz**
   - Modales no cargan correctamente
   - Filtros de tabla no funcionan
   - Paginación con muchos registros

4. **Base de Datos**
   - Triggers de historial
   - Constraints de integridad
   - Transacciones fallidas

## 📖 Documentación para QA

### Flujos de Usuario Implementados

1. **Login** → Dashboard
2. **Usuarios**: Crear → Editar → Ver Historial → Eliminar
3. **Empleados**: Vincular Usuario → Cambiar Área → Ver Timeline
4. **Áreas**: Crear → Asignar Responsable → Gestionar

### Estados y Transiciones

```
Usuario: ACTIVO ↔ INACTIVO ↔ VACACIONES
Empleado: [Área A] → [Área B] (registra TRANSFERENCIA)
Área: Responsable X → Responsable Y (registra ASCENSO_JEFE/DESCENSO)
Equipo: ACTIVO → ASIGNADO → DEVUELTO → MANTENIMIENTO → SUSPENDIDO
Reporte: ABIERTO → EN_PROCESO → RESUELTO/CERRADO
```

## 🖥️ Navegación del Sistema

### Menú Principal (implementado)
- **Dashboard**: Vista general del sistema
- **Usuarios**: Gestión completa de usuarios y empleados  
- **Áreas**: Gestión de departamentos y responsables
- **Equipos**: Control de inventario por tipos
- **Asignaciones**: Gestión de entregas y devoluciones
- **Reportes**: Sistema de incidencias y seguimiento
- **Marcas**: Catálogo de marcas para equipos
- **Ubicaciones**: Gestión de ubicaciones físicas

### Patrones de Interfaz Consistentes
- **Tablas Ajax**: Todas con filtros, paginación y búsqueda
- **Modales Bootstrap**: Para todas las operaciones CRUD
- **Botones Contextuales**: Ver (azul), Editar (amarillo), Historial (verde), Eliminar (rojo)
- **Alerts Dinámicos**: Confirmaciones y errores en tiempo real

## 🤝 Contribución

### Tipos de Contribución Buscados

- 🧪 **Test Cases**: Casos de prueba manuales y automatizados
- 🐛 **Bug Reports**: Identificación de errores
- 📊 **Performance Testing**: Carga de datos, tiempo de respuesta
- 🔒 **Security Testing**: Vulnerabilidades, autenticación
- 📖 **Documentación**: Casos de uso, manuales de usuario

### Guías de Contribución

Ver [CONTRIBUTING.md](CONTRIBUTING.md) para detalles específicos de testing.

## 📝 Troubleshooting

### Problemas Comunes

#### Error de Conexión a BD
```bash
# Verificar que PostgreSQL esté corriendo
sudo systemctl status postgresql

# Verificar que la BD existe
psql -U postgres -l | grep inventario
```

#### Error de Migraciones Flyway
```bash
# Verificar estado de migraciones
mvn flyway:info

# Reparar si es necesario
mvn flyway:repair
```

#### Sin Usuarios para Login
```sql
-- Crear usuario admin temporal
INSERT INTO usuario (codigo, usuario, primer_nombre, primer_apellido, estado, fecha_ingreso, is_admin, password) 
VALUES ('ADMIN', 'admin', 'Admin', 'Temporal', 'ACTIVO', CURRENT_DATE, true, '$2a$10$[BCrypt-hash-aqui]');
```

## 📄 Licencia

Proyecto académico para curso de Aseguramiento de la Calidad del Software.

## 📞 Soporte

- **Issues**: Usar el sistema de GitHub Issues
- **Testing**: Reportar bugs con pasos para reproducir
- **Mejoras**: Sugerir casos de prueba adicionales

---

> **Nota QA**: Sistema completamente funcional con todos los módulos implementados. Incluye 7 módulos principales con interfaces Ajax, validaciones de negocio y triggers de base de datos. Ideal para testing exhaustivo de aplicaciones empresariales Spring Boot.