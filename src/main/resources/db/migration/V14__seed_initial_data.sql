-- ============================================
-- SEED DATA - Datos iniciales para E2E tests
-- ============================================
-- Este script inserta datos iniciales necesarios para:
-- - Tests E2E con Selenium
-- - Desarrollo local
-- Password para admin2: "admin" (BCrypt hash)

-- Insertar usuario administrador (admin2/admin)
-- Password BCrypt hash for "admin": $2a$10$ac0kJkt5wrPTjexmCyjvkeOael5pC3GqoywQQ.oNfgiZKz16ppVra
INSERT INTO usuario (codigo, usuario, password, primer_nombre, primer_apellido, segundo_nombre, segundo_apellido,
                     estado, is_admin, fecha_ingreso, fecha_commit)
VALUES ('ADM001', 'admin2', '$2a$10$ac0kJkt5wrPTjexmCyjvkeOael5pC3GqoywQQ.oNfgiZKz16ppVra',
        'Administrador', 'Sistema', NULL, NULL,
        'ACTIVO', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Obtener el ID del usuario admin para referencias
DO $$
DECLARE
    admin_id INTEGER;
BEGIN
    SELECT id INTO admin_id FROM usuario WHERE codigo = 'ADM001';

    -- Insertar áreas básicas
    INSERT INTO area (nombre, activo, creado_por, fecha_commit)
    VALUES
        ('Recursos Humanos', true, admin_id, CURRENT_TIMESTAMP),
        ('Tecnología', true, admin_id, CURRENT_TIMESTAMP),
        ('Administración', true, admin_id, CURRENT_TIMESTAMP),
        ('Ventas', true, admin_id, CURRENT_TIMESTAMP);

    -- Insertar marcas básicas
    INSERT INTO marca (nombre, activo, creado_por, fecha_commit)
    VALUES
        ('HP', true, admin_id, CURRENT_TIMESTAMP),
        ('Dell', true, admin_id, CURRENT_TIMESTAMP),
        ('Lenovo', true, admin_id, CURRENT_TIMESTAMP),
        ('Apple', true, admin_id, CURRENT_TIMESTAMP),
        ('Samsung', true, admin_id, CURRENT_TIMESTAMP),
        ('Toyota', true, admin_id, CURRENT_TIMESTAMP),
        ('Honda', true, admin_id, CURRENT_TIMESTAMP),
        ('IKEA', true, admin_id, CURRENT_TIMESTAMP);

    -- Insertar ubicaciones básicas
    INSERT INTO ubicacion (nombre, activo, creado_por, fecha_commit)
    VALUES
        ('Oficina Central', true, admin_id, CURRENT_TIMESTAMP),
        ('Sucursal Norte', true, admin_id, CURRENT_TIMESTAMP),
        ('Sucursal Sur', true, admin_id, CURRENT_TIMESTAMP),
        ('Bodega Principal', true, admin_id, CURRENT_TIMESTAMP),
        ('Centro de Distribución', true, admin_id, CURRENT_TIMESTAMP);

    -- Insertar causas de reporte básicas
    INSERT INTO causa (nombre, descripcion, prioridad_reporte, activo, creado_por, fecha_commit)
    VALUES
        ('Daño Físico', 'Equipo presenta daño físico visible', 'ALTA', true, admin_id, CURRENT_TIMESTAMP),
        ('Falla de Software', 'Problemas con el sistema operativo o aplicaciones', 'MEDIA', true, admin_id, CURRENT_TIMESTAMP),
        ('Falla de Hardware', 'Componente de hardware no funciona correctamente', 'ALTA', true, admin_id, CURRENT_TIMESTAMP),
        ('Pérdida', 'Equipo extraviado o perdido', 'CRITICA', true, admin_id, CURRENT_TIMESTAMP),
        ('Robo', 'Equipo robado o sustraído', 'CRITICA', true, admin_id, CURRENT_TIMESTAMP),
        ('Desgaste Normal', 'Desgaste por uso normal del equipo', 'BAJA', true, admin_id, CURRENT_TIMESTAMP);

END $$;