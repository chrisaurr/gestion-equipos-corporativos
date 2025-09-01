-- V7__remove_extraviada_from_estado_devolucion.sql
-- Eliminar valor EXTRAVIADA del enum estado_devolucion

-- Paso 1: Verificar que no hay registros con EXTRAVIADA (solo por seguridad)
-- Si hubiera registros, primero los cambiaríamos a otro estado
DO $$
DECLARE
    extraviada_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO extraviada_count 
    FROM historial_asignacion 
    WHERE estado_devolucion = 'EXTRAVIADA';
    
    IF extraviada_count > 0 THEN
        RAISE NOTICE 'Encontrados % registros con estado EXTRAVIADA. Se cambiarán a PENDIENTE.', extraviada_count;
        
        -- Cambiar todos los registros EXTRAVIADA a PENDIENTE
        UPDATE historial_asignacion 
        SET estado_devolucion = 'PENDIENTE' 
        WHERE estado_devolucion = 'EXTRAVIADA';
        
        RAISE NOTICE 'Registros actualizados correctamente.';
    ELSE
        RAISE NOTICE 'No se encontraron registros con estado EXTRAVIADA.';
    END IF;
END $$;

-- Paso 2: Eliminar el default de la columna temporalmente
ALTER TABLE historial_asignacion ALTER COLUMN estado_devolucion DROP DEFAULT;

-- Paso 3: Crear nuevo enum sin EXTRAVIADA
CREATE TYPE estado_devolucion_new AS ENUM ('PENDIENTE', 'CONFIRMADA');

-- Paso 4: Actualizar la columna para usar el nuevo enum
ALTER TABLE historial_asignacion 
ALTER COLUMN estado_devolucion TYPE estado_devolucion_new 
USING estado_devolucion::text::estado_devolucion_new;

-- Paso 5: Restaurar el default (PENDIENTE)
ALTER TABLE historial_asignacion ALTER COLUMN estado_devolucion SET DEFAULT 'PENDIENTE';

-- Paso 6: Eliminar el enum anterior y renombrar el nuevo
DROP TYPE estado_devolucion;
ALTER TYPE estado_devolucion_new RENAME TO estado_devolucion;

-- Verificación final
DO $$
BEGIN
    RAISE NOTICE 'Migración completada. El enum estado_devolucion ahora solo contiene: PENDIENTE, CONFIRMADA';
END $$;