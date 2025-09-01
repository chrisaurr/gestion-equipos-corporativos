-- Migración V4: Actualizar equipos para usar usuarios en lugar de empleados
-- Actualizar ENUMs y agregar estado_devolucion

-- 1. Crear nuevo ENUM estado_devolucion
CREATE TYPE estado_devolucion AS ENUM ('PENDIENTE', 'CONFIRMADA', 'EXTRAVIADA');

-- 2. Actualizar ENUM motivo_cambio con nuevos valores
DROP TYPE motivo_cambio CASCADE;
CREATE TYPE motivo_cambio AS ENUM (
    'ASIGNACION_INICIAL',
    'USUARIO_INACTIVADO', 
    'REASIGNACION_ADMINISTRATIVA',
    'CAMBIO_AREA',
    'CAMBIO_PUESTO', 
    'DEVOLUCION_VOLUNTARIA'
);

-- 3. Actualizar tabla equipo: id_empleado -> id_usuario  
ALTER TABLE equipo DROP COLUMN id_empleado;
ALTER TABLE equipo ADD COLUMN id_usuario INTEGER REFERENCES usuario(id);

-- 4. Actualizar tabla historial_asignacion
-- Eliminar FK constraints primero
ALTER TABLE historial_asignacion DROP CONSTRAINT historial_asignacion_id_empleado_anterior_fkey;
ALTER TABLE historial_asignacion DROP CONSTRAINT historial_asignacion_id_empleado_nuevo_fkey;

-- Eliminar columnas de empleado
ALTER TABLE historial_asignacion 
DROP COLUMN id_empleado_anterior,
DROP COLUMN id_empleado_nuevo;

-- Agregar columnas de usuario
ALTER TABLE historial_asignacion 
ADD COLUMN id_usuario_anterior INTEGER REFERENCES usuario(id),
ADD COLUMN id_usuario_nuevo INTEGER REFERENCES usuario(id),
ADD COLUMN estado_devolucion estado_devolucion DEFAULT 'PENDIENTE';

-- Recrear columna motivo_cambio con el nuevo ENUM (ya fue eliminada por CASCADE)
ALTER TABLE historial_asignacion ADD COLUMN motivo_cambio motivo_cambio NOT NULL;

-- CRÍTICO: Cambiar asignado_por de NOT NULL a nullable para triggers
ALTER TABLE historial_asignacion ALTER COLUMN asignado_por DROP NOT NULL;

-- 5. Crear función para devolución automática al inactivar usuario
CREATE OR REPLACE FUNCTION fn_devolver_equipos_usuario_inactivo()
RETURNS TRIGGER AS $$
BEGIN
    -- Si usuario cambió de ACTIVO a INACTIVO
    IF OLD.estado = 'ACTIVO' AND NEW.estado = 'INACTIVO' THEN
        
        -- Crear historial de devolución para TODOS sus equipos
        INSERT INTO historial_asignacion (
            id_equipo, id_usuario_anterior, id_usuario_nuevo,
            fecha_asignacion, fecha_devolucion, motivo_cambio,
            observaciones, asignado_por, estado_devolucion
        )
        SELECT 
            e.id,
            NEW.id,
            NULL,
            -- Buscar última asignación o usar fecha actual
            COALESCE(
                (SELECT fecha_asignacion FROM historial_asignacion 
                 WHERE id_equipo = e.id AND id_usuario_nuevo = NEW.id
                 ORDER BY fecha_commit DESC LIMIT 1),
                NOW()
            ),
            NOW(),
            'USUARIO_INACTIVADO',
            'Devolución automática: usuario inactivado por trigger',
            NULL, -- NULL porque el trigger no sabe quién inactivó al usuario
            'PENDIENTE'
        FROM equipo e 
        WHERE e.id_usuario = NEW.id;
        
        -- Quitar asignación de equipos (disponibles para reasignar)
        UPDATE equipo 
        SET id_usuario = NULL
        WHERE id_usuario = NEW.id;
        
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 6. Crear trigger para devolución automática (se ejecuta ANTES que trg_usuario_reingreso alfabéticamente)
CREATE TRIGGER trg_devolver_equipos_usuario_inactivo
    AFTER UPDATE ON usuario
    FOR EACH ROW
    EXECUTE FUNCTION fn_devolver_equipos_usuario_inactivo();

-- 7. Crear índices para mejorar rendimiento
CREATE INDEX idx_equipo_usuario ON equipo(id_usuario);
CREATE INDEX idx_historial_usuario_anterior ON historial_asignacion(id_usuario_anterior);
CREATE INDEX idx_historial_usuario_nuevo ON historial_asignacion(id_usuario_nuevo);
CREATE INDEX idx_historial_estado_devolucion ON historial_asignacion(estado_devolucion);