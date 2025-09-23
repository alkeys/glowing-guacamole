-- ================================
-- 1. USUARIOS
-- ================================
INSERT INTO usuarios (nombre_usuario, contrasena_hash, rol) VALUES
                                                                ('juancliente',  crypt('1234', gen_salt('bf')), 'cliente'),
                                                                ('mariacliente', crypt('abcd', gen_salt('bf')), 'cliente'),
                                                                ('tecnico1',     crypt('pass1', gen_salt('bf')), 'tecnico'),
                                                                ('tecnico2',     crypt('pass2', gen_salt('bf')), 'tecnico'),
                                                                ('admin1',       crypt('adminpass', gen_salt('bf')), 'administrador');

-- ================================
-- 2. CLIENTES
-- ================================
INSERT INTO clientes (id_usuario, nombre_completo, correo, telefono) VALUES
                                                                         (1, 'Juan Pérez',  'juan@example.com',  '555-1001'),
                                                                         (2, 'María Gómez', 'maria@example.com', '555-1002');

-- ================================
-- 3. TÉCNICOS
-- ================================
INSERT INTO tecnicos (id_usuario, nombre_completo, especialidad, activo) VALUES
                                                                             (3, 'Carlos López', 'Hardware', TRUE),
                                                                             (4, 'Ana Martínez', 'Redes',   TRUE);

-- ================================
-- 4. TIPOS DE SERVICIO
-- ================================
INSERT INTO tipos_servicio (nombre_tipo) VALUES
                                             ('Hardware'),
                                             ('Software'),
                                             ('Redes');

-- ================================
-- 5. ESTADOS DEL TICKET
-- ================================
INSERT INTO estados_ticket (nombre_estado) VALUES
                                               ('Abierto'),
                                               ('En proceso'),
                                               ('Cerrado');

-- ================================
-- 6. TICKETS
-- ================================
INSERT INTO tickets
(id_cliente, id_tipo_servicio, fecha_solicitud, id_tecnico, id_estado,
 fecha_asignacion, fecha_cierre, diagnostico, solucion)
VALUES
-- Ticket en proceso
(1, 1, CURRENT_DATE - INTERVAL '5 days', 1, 2,
 CURRENT_DATE - INTERVAL '4 days', NULL,
 'PC no enciende, posible fuente dañada', NULL),

-- Ticket cerrado
(2, 2, CURRENT_DATE - INTERVAL '10 days', 2, 3,
 CURRENT_DATE - INTERVAL '9 days', CURRENT_DATE - INTERVAL '2 days',
 'Error de sistema operativo', 'Reinstalación completa de Windows 11'),

-- Ticket abierto sin asignar técnico
(1, 3, CURRENT_DATE, NULL, 1,
 NULL, NULL,
 'Problemas de conectividad en red doméstica', NULL);
