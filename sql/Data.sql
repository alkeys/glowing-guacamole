-- ===================================
-- SÓLO INSERCIÓN DE DATOS DE EJEMPLO
-- ===================================

-- 1. Catálogo (tipos_servicio y estados_ticket)
-------------------------------------------------
INSERT INTO tipos_servicio (nombre_tipo) VALUES
('Hardware'),
('Software'),
('Redes'),
('Periféricos');

INSERT INTO estados_ticket (nombre_estado) VALUES
('Abierto'),
('En proceso'),
('Cerrado'),
('Pendiente de Cliente');

-- 2. Usuarios
-------------------------------------------------
-- Estos ID (1, 2, 3, 4, 5) serán referenciados por Clientes y Técnicos
-- Nota: 'contrasena_hash' son valores placeholder por seguridad
INSERT INTO usuarios (nombre_usuario, contrasena_hash, rol) VALUES
('jdoe', '$2b$10$HASH123ABCDEFGHIJKLMNOPQRSTUVWXYZ...', 'cliente'),
('asmith', '$2b$10$HASH456DEFGHIJKLMNOPQRSTUVWXYZABC...', 'cliente'),
('pmartinez', '$2b$10$HASH789QRSTUVWXYZABCDEFGHIJKLMNOP...', 'tecnico'),
('cgarcia', '$2b$10$HASH012ABCDEFGHIJKLMNOPQRSTUVWXY...', 'tecnico'),
('admin_sys', '$2b$10$HASH345BCDEFGHIJKLMNOPQRSTUVWXYZA...', 'administrador');

-- 3. Clientes y Técnicos (Usan IDs de Usuarios)
-------------------------------------------------
-- Asume que los clientes obtienen id_cliente 1 y 2
INSERT INTO clientes (id_usuario, nombre_completo, correo, telefono) VALUES
(1, 'Juan Doe', 'juan.doe@example.com', '555-1234'), -- id_cliente = 1
(2, 'Ana Smith', 'ana.smith@example.com', '555-5678');  -- id_cliente = 2

-- Asume que los técnicos obtienen id_tecnico 1 y 2
INSERT INTO tecnicos (id_usuario, nombre_completo, especialidad, activo) VALUES
(3, 'Pedro Martínez', 'Redes', TRUE), -- id_tecnico = 1
(4, 'Carla García', 'Hardware', TRUE); -- id_tecnico = 2

-- 4. Tickets (Usan todos los IDs generados previamente)
-------------------------------------------------
-- Referencias clave:
-- id_cliente: 1=Juan, 2=Ana
-- id_tipo_servicio: 1=Hardware, 2=Software, 3=Redes
-- id_tecnico: 1=Pedro, 2=Carla
-- id_estado: 1=Abierto, 2=En proceso, 3=Cerrado
INSERT INTO tickets (id_cliente, id_tipo_servicio, fecha_solicitud, id_tecnico, id_estado, fecha_asignacion, fecha_cierre, diagnostico, solucion) VALUES
(
    1, -- Cliente: Juan Doe
    2, -- Tipo: Software
    '2025-10-01',
    1, -- Técnico: Pedro Martínez
    3, -- Estado: Cerrado
    '2025-10-01',
    '2025-10-02',
    'Falla de login en sistema ERP.',
    'Se verificó la base de datos de usuarios y se reiniciaron los servicios de autenticación.'
),
(
    2, -- Cliente: Ana Smith
    3, -- Tipo: Redes
    '2025-10-02',
    1, -- Técnico: Pedro Martínez
    2, -- Estado: En proceso
    '2025-10-02',
    NULL, -- No cerrado
    'Conexión a internet intermitente en el área de contabilidad.',
    NULL -- No solucionado
),
(
    1, -- Cliente: Juan Doe
    1, -- Tipo: Hardware
    '2025-10-03',
    2, -- Técnico: Carla García
    1, -- Estado: Abierto
    NULL, -- No asignado
    NULL, -- No cerrado
    'La impresora de red no responde y da error E-05.',
    NULL -- No solucionado
);