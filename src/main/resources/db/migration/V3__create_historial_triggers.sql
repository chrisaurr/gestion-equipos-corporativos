-- Función para registrar cambios en empleado (CAMBIO_CARGO y TRANSFERENCIA)
CREATE OR REPLACE FUNCTION registrar_cambio_empleado()
RETURNS TRIGGER AS $$
BEGIN
    -- TRANSFERENCIA: Solo cuando cambia el área
    IF (OLD.id_area IS DISTINCT FROM NEW.id_area) THEN
        INSERT INTO historial_empleado (
            id_empleado, 
            id_area_anterior, 
            id_area_nueva,
            cargo_anterior, 
            cargo_nuevo,
            tipo_cambio,
            motivo,
            fecha_cambio
        ) VALUES (
            NEW.id,
            OLD.id_area,
            NEW.id_area,
            OLD.cargo,
            NEW.cargo,
            'TRANSFERENCIA'::tipo_cambio_empleado,
            'Transferencia automática',
            NOW()
        );
    
    -- CAMBIO_CARGO: Solo cuando MISMA área pero cargo diferente
    ELSIF (OLD.id_area = NEW.id_area AND OLD.cargo IS DISTINCT FROM NEW.cargo) THEN
        INSERT INTO historial_empleado (
            id_empleado,
            id_area_anterior, 
            id_area_nueva,
            cargo_anterior, 
            cargo_nuevo,
            tipo_cambio,
            motivo,
            fecha_cambio
        ) VALUES (
            NEW.id,
            OLD.id_area,
            NEW.id_area,
            OLD.cargo,
            NEW.cargo,
            'CAMBIO_CARGO'::tipo_cambio_empleado,
            'Cambio de cargo automático',
            NOW()
        );
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Función para registrar reingresos (REINGRESO)
CREATE OR REPLACE FUNCTION registrar_reingreso_usuario()
RETURNS TRIGGER AS $$
BEGIN
    -- Detectar reingreso (INACTIVO -> ACTIVO)
    IF (OLD.estado = 'INACTIVO' AND NEW.estado = 'ACTIVO') THEN
        -- Registrar reingreso si el usuario tiene empleado asociado
        INSERT INTO historial_empleado (
            id_empleado,
            tipo_cambio,
            motivo,
            fecha_cambio
        )
        SELECT 
            e.id,
            'REINGRESO'::tipo_cambio_empleado,
            'Usuario reactivado',
            NOW()
        FROM empleado e 
        WHERE e.id_usuario = NEW.id;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Crear triggers
CREATE TRIGGER trg_empleado_cambio
    AFTER UPDATE ON empleado
    FOR EACH ROW
    EXECUTE FUNCTION registrar_cambio_empleado();

CREATE TRIGGER trg_usuario_reingreso
    AFTER UPDATE ON usuario
    FOR EACH ROW
    EXECUTE FUNCTION registrar_reingreso_usuario();