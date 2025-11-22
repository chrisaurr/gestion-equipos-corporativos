-- ============================================
-- CLEANUP AND RE-SEED SCRIPT FOR E2E TESTS
-- ============================================
-- Este script limpia TODAS las tablas y restaura solo los datos iniciales
-- Se ejecuta antes de cada suite de tests E2E para garantizar un estado limpio

-- Paso 1: Truncar todas las tablas en orden (respetando foreign keys)
-- Orden: desde hijos hacia padres
TRUNCATE TABLE imagen CASCADE;
TRUNCATE TABLE reporte CASCADE;
TRUNCATE TABLE historial_empleado CASCADE;
TRUNCATE TABLE historial_asignacion CASCADE;
TRUNCATE TABLE vehiculo, electronico, mobiliario, herramienta CASCADE;
TRUNCATE TABLE equipo CASCADE;
TRUNCATE TABLE empleado CASCADE;
TRUNCATE TABLE area CASCADE;
TRUNCATE TABLE marca, ubicacion, causa CASCADE;
TRUNCATE TABLE usuario CASCADE;

-- Paso 2: Resetear secuencias (AUTO_INCREMENT)
ALTER SEQUENCE usuario_id_seq RESTART WITH 1;
ALTER SEQUENCE area_id_seq RESTART WITH 1;
ALTER SEQUENCE empleado_id_seq RESTART WITH 1;
ALTER SEQUENCE marca_id_seq RESTART WITH 1;
ALTER SEQUENCE ubicacion_id_seq RESTART WITH 1;
ALTER SEQUENCE causa_id_seq RESTART WITH 1;
ALTER SEQUENCE equipo_id_seq RESTART WITH 1;
ALTER SEQUENCE vehiculo_id_seq RESTART WITH 1;
ALTER SEQUENCE electronico_id_seq RESTART WITH 1;
ALTER SEQUENCE mobiliario_id_seq RESTART WITH 1;
ALTER SEQUENCE herramienta_id_seq RESTART WITH 1;
ALTER SEQUENCE reporte_id_seq RESTART WITH 1;
ALTER SEQUENCE imagen_id_seq RESTART WITH 1;
ALTER SEQUENCE historial_asignacion_id_seq RESTART WITH 1;
ALTER SEQUENCE historial_empleado_id_seq RESTART WITH 1;

-- Paso 3: Re-insertar datos iniciales (SEED DATA)
-- Usuario administrador (admin2/admin)
-- Password BCrypt hash for "admin": $2a$10$ac0kJkt5wrPTjexmCyjvkeOael5pC3GqoywQQ.oNfgiZKz16ppVra
INSERT INTO usuario (codigo, usuario, password, primer_nombre, primer_apellido, segundo_nombre, segundo_apellido,
                     estado, is_admin, fecha_ingreso, fecha_commit)
VALUES ('ADM001', 'admin2', '$2a$10$ac0kJkt5wrPTjexmCyjvkeOael5pC3GqoywQQ.oNfgiZKz16ppVra',
        'Administrador', 'Sistema', NULL, NULL,
        'ACTIVO', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insertar áreas básicas (usando subquery para obtener admin_id)
INSERT INTO area (nombre, activo, creado_por, fecha_commit)
VALUES
    ('Recursos Humanos', true, (SELECT id FROM usuario WHERE codigo = 'ADM001'), CURRENT_TIMESTAMP),
    ('Tecnología', true, (SELECT id FROM usuario WHERE codigo = 'ADM001'), CURRENT_TIMESTAMP),
    ('Administración', true, (SELECT id FROM usuario WHERE codigo = 'ADM001'), CURRENT_TIMESTAMP),
    ('Ventas', true, (SELECT id FROM usuario WHERE codigo = 'ADM001'), CURRENT_TIMESTAMP);

-- Insertar marcas básicas
INSERT INTO marca (nombre, activo, creado_por, fecha_commit)
VALUES
    ('HP', true, (SELECT id FROM usuario WHERE codigo = 'ADM001'), CURRENT_TIMESTAMP),
    ('Dell', true, (SELECT id FROM usuario WHERE codigo = 'ADM001'), CURRENT_TIMESTAMP),
    ('Lenovo', true, (SELECT id FROM usuario WHERE codigo = 'ADM001'), CURRENT_TIMESTAMP),
    ('Apple', true, (SELECT id FROM usuario WHERE codigo = 'ADM001'), CURRENT_TIMESTAMP),
    ('Samsung', true, (SELECT id FROM usuario WHERE codigo = 'ADM001'), CURRENT_TIMESTAMP),
    ('Toyota', true, (SELECT id FROM usuario WHERE codigo = 'ADM001'), CURRENT_TIMESTAMP),
    ('Honda', true, (SELECT id FROM usuario WHERE codigo = 'ADM001'), CURRENT_TIMESTAMP),
    ('IKEA', true, (SELECT id FROM usuario WHERE codigo = 'ADM001'), CURRENT_TIMESTAMP);

-- Insertar ubicaciones básicas
INSERT INTO ubicacion (nombre, activo, creado_por, fecha_commit)
VALUES
    ('Oficina Central', true, (SELECT id FROM usuario WHERE codigo = 'ADM001'), CURRENT_TIMESTAMP),
    ('Sucursal Norte', true, (SELECT id FROM usuario WHERE codigo = 'ADM001'), CURRENT_TIMESTAMP),
    ('Sucursal Sur', true, (SELECT id FROM usuario WHERE codigo = 'ADM001'), CURRENT_TIMESTAMP),
    ('Bodega Principal', true, (SELECT id FROM usuario WHERE codigo = 'ADM001'), CURRENT_TIMESTAMP),
    ('Centro de Distribución', true, (SELECT id FROM usuario WHERE codigo = 'ADM001'), CURRENT_TIMESTAMP);

-- Insertar causas de reporte básicas
INSERT INTO causa (nombre, descripcion, prioridad_reporte, activo, creado_por, fecha_commit)
VALUES
    ('Daño Físico', 'Equipo presenta daño físico visible', 'ALTA', true, (SELECT id FROM usuario WHERE codigo = 'ADM001'), CURRENT_TIMESTAMP),
    ('Falla de Software', 'Problemas con el sistema operativo o aplicaciones', 'MEDIA', true, (SELECT id FROM usuario WHERE codigo = 'ADM001'), CURRENT_TIMESTAMP),
    ('Falla de Hardware', 'Componente de hardware no funciona correctamente', 'ALTA', true, (SELECT id FROM usuario WHERE codigo = 'ADM001'), CURRENT_TIMESTAMP),
    ('Pérdida', 'Equipo extraviado o perdido', 'CRITICA', true, (SELECT id FROM usuario WHERE codigo = 'ADM001'), CURRENT_TIMESTAMP),
    ('Robo', 'Equipo robado o sustraído', 'CRITICA', true, (SELECT id FROM usuario WHERE codigo = 'ADM001'), CURRENT_TIMESTAMP),
    ('Desgaste Normal', 'Desgaste por uso normal del equipo', 'BAJA', true, (SELECT id FROM usuario WHERE codigo = 'ADM001'), CURRENT_TIMESTAMP);