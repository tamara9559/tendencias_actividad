-- DDL: crea esquema e índices para MySQL 8+
SET FOREIGN_KEY_CHECKS = 0;

CREATE DATABASE IF NOT EXISTS ordenes_sla
  CHARACTER SET = 'utf8mb4'
  COLLATE = 'utf8mb4_unicode_ci';
USE ordenes_sla;

-- Tabla: areas
CREATE TABLE IF NOT EXISTS areas (
                                     id INT UNSIGNED NOT NULL AUTO_INCREMENT,
                                     nombre VARCHAR(100) NOT NULL,
    responsable VARCHAR(150) DEFAULT NULL,
    descripcion TEXT DEFAULT NULL,
    creada_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY ux_areas_nombre (nombre)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla: ordenes
CREATE TABLE IF NOT EXISTS ordenes (
                                       id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                                       titulo VARCHAR(255) NOT NULL,
    descripcion TEXT,
    creador VARCHAR(150) NOT NULL,
    estado_global ENUM(
                          'NUEVA',
                          'ASIGNADA',
                          'EN_PROGRESO',
                          'PENDIENTE',
                          'COMPLETADA',
                          'CERRADA_SIN_SOLUCION',
                          'VENCIDA'
                      ) NOT NULL DEFAULT 'NUEVA',
    creada_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizada_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    metadata JSON DEFAULT NULL,
    PRIMARY KEY (id),
    KEY idx_ordenes_estado_global (estado_global),
    KEY idx_ordenes_creada_en (creada_en)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla: orden_area (mapeo orden <-> area)
CREATE TABLE IF NOT EXISTS orden_area (
                                          id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                                          orden_id BIGINT UNSIGNED NOT NULL,
                                          area_id INT UNSIGNED NOT NULL,
                                          asignada_a VARCHAR(150) DEFAULT NULL, -- nombre o identificador del agente
    estado_parcial ENUM(
                           'NUEVA',
                           'ASIGNADA',
                           'EN_PROGRESO',
                           'PENDIENTE',
                           'COMPLETADA',
                           'CERRADA_SIN_SOLUCION',
                           'VENCIDA'
                       ) NOT NULL DEFAULT 'NUEVA',
    seg_acumulados INT UNSIGNED NOT NULL DEFAULT 0,
    ultima_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_orden_area_orden FOREIGN KEY (orden_id) REFERENCES ordenes (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_orden_area_area  FOREIGN KEY (area_id)  REFERENCES areas (id)   ON DELETE RESTRICT ON UPDATE CASCADE,
    INDEX idx_orden_area_orden (orden_id),
    INDEX idx_orden_area_area  (area_id),
    INDEX idx_orden_area_estado (estado_parcial),
    CHECK (seg_acumulados >= 0)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla: historial
CREATE TABLE IF NOT EXISTS historial (
                                         id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                                         orden_id BIGINT UNSIGNED NOT NULL,
                                         evento VARCHAR(150) NOT NULL, -- ej: 'CREACION', 'ASIGNACION_AREA', 'TIEMPO_TIMEOUT', 'CIERRE'
    detalle TEXT DEFAULT NULL,
    estado_global ENUM(
                          'NUEVA',
                          'ASIGNADA',
                          'EN_PROGRESO',
                          'PENDIENTE',
                          'COMPLETADA',
                          'CERRADA_SIN_SOLUCION',
                          'VENCIDA'
                      ) NOT NULL,
    actor VARCHAR(150) DEFAULT NULL, -- quien realizó la acción (usuario / sistema)
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_historial_orden FOREIGN KEY (orden_id) REFERENCES ordenes (id) ON DELETE CASCADE ON UPDATE CASCADE,
    INDEX idx_historial_orden (orden_id),
    INDEX idx_historial_timestamp (timestamp)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET FOREIGN_KEY_CHECKS = 1;
