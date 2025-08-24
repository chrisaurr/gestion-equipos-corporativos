-- Crear ENUM para tipo de cambio empleado
CREATE TYPE tipo_cambio_empleado AS ENUM (
    'CAMBIO_CARGO',
    'TRANSFERENCIA', 
    'ASCENSO_JEFE',
    'DESCENSO',
    'REINGRESO'
);

-- Crear tabla historial_empleado
CREATE TABLE historial_empleado (
    id SERIAL PRIMARY KEY,
    id_empleado INTEGER NOT NULL REFERENCES empleado(id),
    id_area_anterior INTEGER REFERENCES area(id),
    id_area_nueva INTEGER REFERENCES area(id),
    cargo_anterior VARCHAR(100),
    cargo_nuevo VARCHAR(100),
    tipo_cambio tipo_cambio_empleado NOT NULL,
    motivo TEXT,
    creado_por INTEGER REFERENCES usuario(id),
    fecha_cambio TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Crear índices para mejor performance
CREATE INDEX idx_historial_empleado_empleado ON historial_empleado(id_empleado);
CREATE INDEX idx_historial_empleado_fecha ON historial_empleado(fecha_cambio);
CREATE INDEX idx_historial_empleado_tipo ON historial_empleado(tipo_cambio);