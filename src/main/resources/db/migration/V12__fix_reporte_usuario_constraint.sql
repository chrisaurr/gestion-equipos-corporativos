-- Fix foreign key constraint for reporte.id_usuario
-- Remove incorrect constraint that references empleado table
-- Keep correct constraint that references usuario table

-- Drop the incorrect foreign key constraint
ALTER TABLE reporte DROP CONSTRAINT IF EXISTS reporte_id_empleado_fkey;

-- Ensure the correct constraint exists (it should already exist as fk_reporte_usuario)
-- This is just to be safe in case it was accidentally dropped
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints 
        WHERE constraint_name = 'fk_reporte_usuario' 
        AND table_name = 'reporte'
    ) THEN
        ALTER TABLE reporte ADD CONSTRAINT fk_reporte_usuario 
        FOREIGN KEY (id_usuario) REFERENCES usuario(id);
    END IF;
END $$;