-- ================================
-- CREACIÓN DE TABLAS NORMALIZADAS
-- ================================

-- Tabla de clientes
CREATE TABLE clientes (
                          id_cliente SERIAL PRIMARY KEY,
                          nombre_completo VARCHAR(100) NOT NULL,
                          correo VARCHAR(100),
                          telefono VARCHAR(20)
);

-- Tabla de técnicos
CREATE TABLE tecnicos (
                          id_tecnico SERIAL PRIMARY KEY,
                          nombre_completo VARCHAR(100) NOT NULL,
                          especialidad VARCHAR(50) -- ejemplo: hardware, redes, software
);

-- Tabla de tipos de servicio
CREATE TABLE tipos_servicio (
                                id_tipo_servicio SERIAL PRIMARY KEY,
                                nombre_tipo VARCHAR(50) NOT NULL -- hardware, software, redes
);

-- Tabla de estados del ticket
CREATE TABLE estados_ticket (
                                id_estado SERIAL PRIMARY KEY,
                                nombre_estado VARCHAR(30) NOT NULL -- Abierto, En proceso, Cerrado
);

-- Tabla de tickets
CREATE TABLE tickets (
                         id_ticket SERIAL PRIMARY KEY,
                         id_cliente INTEGER NOT NULL REFERENCES clientes(id_cliente),
                         id_tipo_servicio INTEGER NOT NULL REFERENCES tipos_servicio(id_tipo_servicio),
                         fecha_solicitud DATE NOT NULL DEFAULT CURRENT_DATE,
                         id_tecnico INTEGER REFERENCES tecnicos(id_tecnico),
                         id_estado INTEGER NOT NULL REFERENCES estados_ticket(id_estado),
                         fecha_asignacion DATE,
                         fecha_cierre DATE,
                         diagnostico TEXT,
                         solucion TEXT
);

-- ===================
-- INSERCIÓN BÁSICA DE DATOS
-- ===================

-- Estados posibles
INSERT INTO estados_ticket (nombre_estado) VALUES
                                               ('Abierto'), ('En proceso'), ('Cerrado');

-- Tipos de servicio
INSERT INTO tipos_servicio (nombre_tipo) VALUES
                                             ('Hardware'), ('Software'), ('Redes');

-- Técnicos de ejemplo
INSERT INTO tecnicos (nombre_completo, especialidad) VALUES
                                                         ('Luis Torres', 'Hardware'),
                                                         ('Ana Pérez', 'Software'),
                                                         ('Carlos Martínez', 'Redes');

-- Cliente de ejemplo
INSERT INTO clientes (nombre_completo, correo, telefono) VALUES
    ('Juan López', 'juan@example.com', '7070-7070');

-- Ticket de ejemplo
INSERT INTO tickets (
    id_cliente, id_tipo_servicio, fecha_solicitud, id_tecnico, id_estado,
    fecha_asignacion, diagnostico, solucion
) VALUES (
             1, 1, '2025-08-01', 1, 2, '2025-08-02',
             'Fuente de poder defectuosa', 'Se reemplazó la fuente de poder'
         );