-- Migración inicial del esquema de base de datos
-- Sistema de Control de Inventario

-- Crear ENUMs de PostgreSQL
CREATE TYPE estado_usuario AS ENUM ('ACTIVO', 'INACTIVO', 'VACACIONES');
CREATE TYPE estado_equipo AS ENUM ('ACTIVO', 'INACTIVO', 'MANTENIMIENTO', 'SUSPENDIDO');
CREATE TYPE tipo_equipo AS ENUM ('VEHICULO', 'ELECTRONICO', 'MOBILIARIO', 'HERRAMIENTA');
CREATE TYPE alimentacion AS ENUM ('V110', 'V220', 'DIESEL', 'REGULAR', 'SUPER', 'BATERIA', 'NINGUNA');
CREATE TYPE conectividad AS ENUM (
    'BLUETOOTH', 'WIFI', 'GSM', 'NFC',
    'BLUETOOTH_WIFI', 'BLUETOOTH_GSM', 'BLUETOOTH_NFC', 'WIFI_GSM', 'WIFI_NFC', 'GSM_NFC',
    'BLUETOOTH_WIFI_GSM', 'BLUETOOTH_WIFI_NFC', 'BLUETOOTH_GSM_NFC', 'WIFI_GSM_NFC',
    'BLUETOOTH_WIFI_GSM_NFC', 'NINGUNO'
);
CREATE TYPE operador AS ENUM ('STARLINK', 'CLARO', 'TIGO', 'COMNET', 'VERASAT', 'TELECOM', 'NINGUNO');
CREATE TYPE motivo_cambio AS ENUM (
    'ASIGNACION_INICIAL', 'CAMBIO_AREA', 'EMPLEADO_DESPEDIDO', 'EMPLEADO_RENUNCIA',
    'CAMBIO_PUESTO', 'EQUIPO_DANADO', 'REASIGNACION_ADMINISTRATIVA'
);
CREATE TYPE estado_reporte AS ENUM ('ABIERTO', 'EN_PROCESO', 'RESUELTO', 'CERRADO');
CREATE TYPE prioridad_reporte AS ENUM ('BAJA', 'MEDIA', 'ALTA', 'CRITICA');

-- Crear tablas
CREATE TABLE usuario (
    id SERIAL PRIMARY KEY,
    codigo VARCHAR(20) UNIQUE NOT NULL,
    usuario VARCHAR(50) UNIQUE NOT NULL,
    primer_nombre VARCHAR(50) NOT NULL,
    segundo_nombre VARCHAR(50),
    primer_apellido VARCHAR(50) NOT NULL,
    segundo_apellido VARCHAR(50),
    estado estado_usuario DEFAULT 'ACTIVO',
    fecha_ingreso DATE NOT NULL,
    fecha_salida DATE,
    is_admin BOOLEAN NOT NULL DEFAULT FALSE,
    password VARCHAR(100) NOT NULL,
    fecha_commit TIMESTAMP DEFAULT NOW()
);

CREATE TABLE area (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    id_usuario INTEGER REFERENCES usuario(id),
    activo BOOLEAN DEFAULT TRUE,
    creado_por INTEGER NOT NULL REFERENCES usuario(id),
    fecha_commit TIMESTAMP DEFAULT NOW()
);

CREATE TABLE empleado (
    id SERIAL PRIMARY KEY,
    id_usuario INTEGER UNIQUE NOT NULL REFERENCES usuario(id),
    id_area INTEGER NOT NULL REFERENCES area(id),
    cargo VARCHAR(100) NOT NULL,
    observaciones TEXT
);

CREATE TABLE marca (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    activo BOOLEAN DEFAULT TRUE,
    creado_por INTEGER NOT NULL REFERENCES usuario(id),
    fecha_commit TIMESTAMP DEFAULT NOW()
);

CREATE TABLE ubicacion (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(200),
    activo BOOLEAN DEFAULT TRUE,
    creado_por INTEGER NOT NULL REFERENCES usuario(id),
    fecha_commit TIMESTAMP DEFAULT NOW()
);

CREATE TABLE equipo (
    id SERIAL PRIMARY KEY,
    identificador VARCHAR(20),
    nombre VARCHAR(50) NOT NULL,
    id_marca INTEGER NOT NULL REFERENCES marca(id),
    color VARCHAR(50) NOT NULL,
    valor DECIMAL(10,2) NOT NULL,
    serie VARCHAR(50) NOT NULL,
    extras TEXT,
    tipo_equipo tipo_equipo NOT NULL,
    tipo_alimentacion alimentacion DEFAULT 'NINGUNA',
    id_empleado INTEGER REFERENCES empleado(id),
    id_ubicacion INTEGER REFERENCES ubicacion(id),
    estado estado_equipo DEFAULT 'ACTIVO',
    creado_por INTEGER NOT NULL REFERENCES usuario(id),
    fecha_commit TIMESTAMP DEFAULT NOW()
);

CREATE TABLE vehiculo (
    id SERIAL PRIMARY KEY,
    no_motor VARCHAR(20) NOT NULL,
    vin VARCHAR(20) UNIQUE NOT NULL,
    cilindrada INTEGER NOT NULL,
    placa VARCHAR(10) UNIQUE NOT NULL,
    modelo INTEGER NOT NULL,
    id_equipo INTEGER UNIQUE NOT NULL REFERENCES equipo(id)
);

CREATE TABLE electronico (
    id SERIAL PRIMARY KEY,
    imei VARCHAR(20) UNIQUE,
    sistema_operativo VARCHAR(20),
    conectividad conectividad DEFAULT 'NINGUNO',
    operador operador DEFAULT 'NINGUNO',
    id_equipo INTEGER UNIQUE NOT NULL REFERENCES equipo(id)
);

CREATE TABLE mobiliario (
    id SERIAL PRIMARY KEY,
    material VARCHAR(50) NOT NULL,
    altura DECIMAL(8,2) NOT NULL,
    ancho DECIMAL(8,2) NOT NULL,
    profundidad DECIMAL(8,2) NOT NULL,
    cantidad_piezas INTEGER DEFAULT 1,
    id_equipo INTEGER UNIQUE NOT NULL REFERENCES equipo(id)
);

CREATE TABLE herramienta (
    id SERIAL PRIMARY KEY,
    material VARCHAR(50) NOT NULL,
    id_equipo INTEGER UNIQUE NOT NULL REFERENCES equipo(id)
);

CREATE TABLE causa (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    descripcion VARCHAR(300),
    activo BOOLEAN DEFAULT TRUE,
    creado_por INTEGER NOT NULL REFERENCES usuario(id),
    fecha_commit TIMESTAMP DEFAULT NOW()
);

CREATE TABLE reporte (
    id SERIAL PRIMARY KEY,
    observacion TEXT,
    id_causa INTEGER NOT NULL REFERENCES causa(id),
    id_equipo INTEGER NOT NULL REFERENCES equipo(id),
    prioridad prioridad_reporte NOT NULL,
    id_empleado INTEGER NOT NULL REFERENCES empleado(id),
    estado estado_reporte DEFAULT 'ABIERTO',
    fecha_commit TIMESTAMP DEFAULT NOW()
);

CREATE TABLE imagen (
    id SERIAL PRIMARY KEY,
    url TEXT NOT NULL,
    id_reporte INTEGER NOT NULL REFERENCES reporte(id),
    activo BOOLEAN DEFAULT TRUE,
    creado_por INTEGER NOT NULL REFERENCES usuario(id),
    fecha_commit TIMESTAMP DEFAULT NOW()
);

CREATE TABLE historial_asignacion (
    id SERIAL PRIMARY KEY,
    id_equipo INTEGER NOT NULL REFERENCES equipo(id),
    id_empleado_anterior INTEGER REFERENCES empleado(id),
    id_empleado_nuevo INTEGER REFERENCES empleado(id),
    fecha_asignacion TIMESTAMP NOT NULL,
    fecha_devolucion TIMESTAMP,
    motivo_cambio motivo_cambio NOT NULL,
    observaciones TEXT,
    asignado_por INTEGER NOT NULL REFERENCES usuario(id),
    fecha_commit TIMESTAMP DEFAULT NOW()
);

-- Crear índices para mejorar rendimiento
CREATE INDEX idx_usuario_usuario ON usuario(usuario);
CREATE INDEX idx_usuario_codigo ON usuario(codigo);
CREATE INDEX idx_equipo_identificador ON equipo(identificador);
CREATE INDEX idx_equipo_estado ON equipo(estado);
CREATE INDEX idx_equipo_empleado ON equipo(id_empleado);
CREATE INDEX idx_historial_equipo ON historial_asignacion(id_equipo);
CREATE INDEX idx_reporte_estado ON reporte(estado);
CREATE INDEX idx_reporte_equipo ON reporte(id_equipo);