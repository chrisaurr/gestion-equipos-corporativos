-- Migración V5: Agregar trigger para identificador automático de equipos

-- 1. Crear función para generar identificador automático
CREATE OR REPLACE FUNCTION generar_identificador_equipo()
RETURNS TRIGGER AS $$
DECLARE
    nuevo_numero INTEGER;
    anio_actual INTEGER;
    nuevo_identificador VARCHAR(15);
BEGIN
    -- Solo generar si identificador es NULL
    IF NEW.identificador IS NULL THEN
        anio_actual := EXTRACT(YEAR FROM NOW());
        
        -- Buscar el último número usado para el año actual
        SELECT COALESCE(
            MAX(
                CAST(
                    SUBSTRING(identificador FROM 'EQ-\d{4}-(\d{5})') AS INTEGER
                )
            ), 0
        ) + 1
        INTO nuevo_numero
        FROM equipo 
        WHERE identificador ~ ('^EQ-' || anio_actual::text || '-\d{5}$');
        
        -- Generar nuevo identificador con formato EQ-YYYY-NNNNN
        nuevo_identificador := 'EQ-' || anio_actual::text || '-' || LPAD(nuevo_numero::text, 5, '0');
        
        NEW.identificador := nuevo_identificador;
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 2. Crear trigger que se ejecuta ANTES del INSERT
CREATE TRIGGER trg_equipo_generar_identificador
    BEFORE INSERT ON equipo
    FOR EACH ROW
    EXECUTE FUNCTION generar_identificador_equipo();

-- 3. Crear índice para mejorar rendimiento de consultas por identificador (solo si no existe)
CREATE INDEX IF NOT EXISTS idx_equipo_identificador ON equipo(identificador);

-- 4. Comentarios para documentación
COMMENT ON FUNCTION generar_identificador_equipo() IS 'Genera identificador automático para equipos con formato EQ-YYYY-NNNNN cuando identificador es NULL';
COMMENT ON TRIGGER trg_equipo_generar_identificador ON equipo IS 'Trigger que asigna identificador automático a equipos nuevos';