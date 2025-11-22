-- Migración V13: Hacer campos color y serie opcionales (nullable)
-- Fecha: 2025-11-11
-- Motivo: No todos los equipos requieren color específico o número de serie
-- Ejemplo: herramientas genéricas, mobiliario sin serie, etc.

-- Cambiar color a nullable
ALTER TABLE equipo ALTER COLUMN color DROP NOT NULL;

-- Cambiar serie a nullable
ALTER TABLE equipo ALTER COLUMN serie DROP NOT NULL;

-- Comentarios explicativos
COMMENT ON COLUMN equipo.color IS 'Color del equipo (opcional) - no todos los equipos requieren especificar color';
COMMENT ON COLUMN equipo.serie IS 'Número de serie del equipo (opcional) - solo equipos de alto valor o tecnológicos tienen serie';