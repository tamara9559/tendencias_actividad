-- ===============================================
-- Script: V1__seed.sql
-- Descripción: Datos iniciales para el MVP
-- Sistema: Gestión y Enrutamiento de Órdenes Multiárea con Temporizador
-- Base de datos: MySQL
-- ===============================================

-- ===============================================
-- LIMPIEZA PREVIA (solo usar en desarrollo)
-- ===============================================
DELETE FROM historial;
DELETE FROM orden_area;
DELETE FROM ordenes;
DELETE FROM areas;

-- ===============================================
-- ÁREAS (3–5 registros)
-- ===============================================
INSERT INTO areas (id, nombre, responsable, medio_contacto)
VALUES
    (1, 'Soporte Técnico', 'Ana López', 'ana.lopez@empresa.com'),
    (2, 'Desarrollo', 'Carlos Pérez', 'carlos.perez@empresa.com'),
    (3, 'Logística', 'María Torres', 'maria.torres@empresa.com'),
    (4, 'Infraestructura', 'Luis Gómez', 'luis.gomez@empresa.com');

-- ===============================================
-- ÓRDENES (6–10 registros)
-- ===============================================
INSERT INTO ordenes (id, titulo, descripcion, creador, estado_global, creada_en, actualizada_en)
VALUES
    (1, 'Falla en equipo de impresión', 'El equipo HP LaserJet no responde.', 'soporte@empresa.com', 'ASIGNADA', DATE_SUB(UTC_TIMESTAMP(), INTERVAL 6 DAY), DATE_SUB(UTC_TIMESTAMP(), INTERVAL 6 DAY)),
    (2, 'Actualización del sistema ERP', 'Se requiere aplicar el parche 1.2.5.', 'admin@empresa.com', 'ASIGNADA', DATE_SUB(UTC_TIMESTAMP(), INTERVAL 5 DAY), DATE_SUB(UTC_TIMESTAMP(), INTERVAL 5 DAY)),
    (3, 'Solicitud de transporte', 'Se necesita vehículo para traslado de materiales.', 'usuario@empresa.com', 'NUEVA', DATE_SUB(UTC_TIMESTAMP(), INTERVAL 4 DAY), DATE_SUB(UTC_TIMESTAMP(), INTERVAL 4 DAY)),
    (4, 'Revisión de red interna', 'Intermitencia en red del piso 3.', 'infra@empresa.com', 'ASIGNADA', DATE_SUB(UTC_TIMESTAMP(), INTERVAL 3 DAY), DATE_SUB(UTC_TIMESTAMP(), INTERVAL 3 DAY)),
    (5, 'Creación de nueva cuenta de usuario', 'Nuevo colaborador en el área de ventas.', 'rrhh@empresa.com', 'ASIGNADA', DATE_SUB(UTC_TIMESTAMP(), INTERVAL 2 DAY), DATE_SUB(UTC_TIMESTAMP(), INTERVAL 2 DAY)),
    (6, 'Optimización de base de datos', 'Mejorar rendimiento del módulo de reportes.', 'devops@empresa.com', 'ASIGNADA', DATE_SUB(UTC_TIMESTAMP(), INTERVAL 1 DAY), DATE_SUB(UTC_TIMESTAMP(), INTERVAL 1 DAY));

-- ===============================================
-- ORDEN_AREA (asignaciones, 2–4 multiárea)
-- ===============================================
INSERT INTO orden_area (id, orden_id, area_id, asignada_a, estado_parcial, seg_acumulados)
VALUES
    (1, 1, 1, 'tecnico1', 'ASIGNADA', 0),
    (2, 2, 2, 'dev1', 'ASIGNADA', 0),
    (3, 3, 3, 'log1', 'NUEVA', 0),
    (4, 4, 1, 'net1', 'ASIGNADA', 0),
    (5, 4, 4, 'infra1', 'ASIGNADA', 0),
    (6, 5, 1, 'soporte2', 'ASIGNADA', 0),
    (7, 5, 2, 'dev2', 'ASIGNADA', 0),
    (8, 6, 2, 'devops1', 'ASIGNADA', 0),
    (9, 6, 4, 'infra2', 'ASIGNADA', 0);

-- ===============================================
-- HISTORIAL (evento inicial de creación)
-- ===============================================
INSERT INTO historial (id, orden_id, evento, detalle, estado_global, timestamp, actor)
VALUES
    (1, 1, 'CREACION', 'Orden creada y asignada al área Soporte Técnico.', 'ASIGNADA', DATE_SUB(UTC_TIMESTAMP(), INTERVAL 6 DAY), 'soporte@empresa.com'),
    (2, 2, 'CREACION', 'Orden creada y asignada a Desarrollo.', 'ASIGNADA', DATE_SUB(UTC_TIMESTAMP(), INTERVAL 5 DAY), 'admin@empresa.com'),
    (3, 3, 'CREACION', 'Orden creada para Logística.', 'NUEVA', DATE_SUB(UTC_TIMESTAMP(), INTERVAL 4 DAY), 'usuario@empresa.com'),
    (4, 4, 'CREACION', 'Orden creada y asignada a Soporte e Infraestructura.', 'ASIGNADA', DATE_SUB(UTC_TIMESTAMP(), INTERVAL 3 DAY), 'infra@empresa.com'),
    (5, 5, 'CREACION', 'Orden creada y asignada a Soporte y Desarrollo.', 'ASIGNADA', DATE_SUB(UTC_TIMESTAMP(), INTERVAL 2 DAY), 'rrhh@empresa.com'),
    (6, 6, 'CREACION', 'Orden creada y asignada a Desarrollo e Infraestructura.', 'ASIGNADA', DATE_SUB(UTC_TIMESTAMP(), INTERVAL 1 DAY), 'devops@empresa.com');
