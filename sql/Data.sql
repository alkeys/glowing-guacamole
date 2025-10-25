-- ================================
-- CREACIÓN DE TABLAS NORMALIZADAS
-- ================================

DROP TABLE IF EXISTS tickets CASCADE;
DROP TABLE IF EXISTS tecnicos CASCADE;
DROP TABLE IF EXISTS clientes CASCADE;
DROP TABLE IF EXISTS estados_ticket CASCADE;
DROP TABLE IF EXISTS tipos_servicio CASCADE;
DROP TABLE IF EXISTS usuarios CASCADE;

CREATE TABLE usuarios (
    id_usuario SERIAL PRIMARY KEY,
    nombre_usuario VARCHAR(50) UNIQUE NOT NULL,
    contrasena_hash VARCHAR(255) NOT NULL,
    rol VARCHAR(20) NOT NULL CHECK (rol IN ('cliente', 'tecnico', 'administrador'))
);

CREATE TABLE clientes (
    id_cliente SERIAL PRIMARY KEY,
    id_usuario INTEGER UNIQUE REFERENCES usuarios(id_usuario),
    nombre_completo VARCHAR(100) NOT NULL,
    correo VARCHAR(100),
    telefono VARCHAR(20)
);

CREATE TABLE tecnicos (
    id_tecnico SERIAL PRIMARY KEY,
    id_usuario INTEGER UNIQUE REFERENCES usuarios(id_usuario),
    nombre_completo VARCHAR(100) NOT NULL,
    especialidad VARCHAR(50),
    activo BOOLEAN DEFAULT TRUE
);

CREATE TABLE tipos_servicio (
    id_tipo_servicio SERIAL PRIMARY KEY,
    nombre_tipo VARCHAR(50) NOT NULL
);

CREATE TABLE estados_ticket (
    id_estado SERIAL PRIMARY KEY,
    nombre_estado VARCHAR(30) NOT NULL
);

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

-- ================================
-- INSERCIÓN DE DATOS
-- ================================

-- Usuarios (10 clientes + 5 técnicos + 2 admins)
INSERT INTO usuarios (nombre_usuario, contrasena_hash, rol) VALUES
('cliente01','hash1','cliente'),('cliente02','hash2','cliente'),('cliente03','hash3','cliente'),
('cliente04','hash4','cliente'),('cliente05','hash5','cliente'),('cliente06','hash6','cliente'),
('cliente07','hash7','cliente'),('cliente08','hash8','cliente'),('cliente09','hash9','cliente'),
('cliente10','hash10','cliente'),
('tecnico01','hash11','tecnico'),('tecnico02','hash12','tecnico'),('tecnico03','hash13','tecnico'),
('tecnico04','hash14','tecnico'),('tecnico05','hash15','tecnico'),
('admin01','hash16','administrador'),('admin02','hash17','administrador');

-- Clientes
INSERT INTO clientes (id_usuario,nombre_completo,correo,telefono) VALUES
(1,'Juan Pérez','juan.perez@mail.com','555-1001'),
(2,'María López','maria.lopez@mail.com','555-1002'),
(3,'Carlos García','carlos.garcia@mail.com','555-1003'),
(4,'Ana Fernández','ana.fernandez@mail.com','555-1004'),
(5,'Luis Martínez','luis.martinez@mail.com','555-1005'),
(6,'Sofía Torres','sofia.torres@mail.com','555-1006'),
(7,'José Ramírez','jose.ramirez@mail.com','555-1007'),
(8,'Laura González','laura.gonzalez@mail.com','555-1008'),
(9,'Pedro Sánchez','pedro.sanchez@mail.com','555-1009'),
(10,'Elena Morales','elena.morales@mail.com','555-1010');

-- Técnicos (IDs de técnicos serán 1-5)
INSERT INTO tecnicos (id_usuario,nombre_completo,especialidad,activo) VALUES
(11,'Raúl Jiménez','Hardware',TRUE),
(12,'Lucía Herrera','Redes',TRUE),
(13,'Miguel Castillo','Software',TRUE),
(14,'Verónica Díaz','Hardware',TRUE),
(15,'Andrés Vega','Redes',TRUE);

-- Tipos de servicio
INSERT INTO tipos_servicio (nombre_tipo) VALUES
('Hardware'),('Software'),('Redes'),('Periféricos');

-- Estados de ticket
INSERT INTO estados_ticket (nombre_estado) VALUES
('Abierto'),('En proceso'),('Cerrado'),('Pendiente de Cliente');

-- ================================
-- GENERACIÓN DE 100 TICKETS ALEATORIOS
-- ================================

-- Usaremos un ciclo en SQL con generate_series (PostgreSQL)
DO $$
DECLARE
    i INT;
    cliente INT;
    tecnico INT;
    tipo INT;
    estado INT;
    dias_antes INT;
BEGIN
    FOR i IN 1..100 LOOP
        cliente := (i % 10) + 1;      -- id_cliente 1-10
        tecnico := ((i % 5) + 1);     -- id_tecnico 1-5
        tipo := ((i % 4) + 1);        -- id_tipo_servicio 1-4
        estado := ((i % 4) + 1);      -- id_estado 1-4
        dias_antes := (i % 30);       -- fecha_solicitud hace hasta 30 días
        INSERT INTO tickets (
            id_cliente,id_tipo_servicio,fecha_solicitud,id_tecnico,id_estado,
            fecha_asignacion,fecha_cierre,diagnostico,solucion
        ) VALUES (
            cliente,
            tipo,
            CURRENT_DATE - dias_antes,
            tecnico,
            estado,
            CURRENT_DATE - (dias_antes-1),
            CASE WHEN estado=3 THEN CURRENT_DATE - (dias_antes-2) ELSE NULL END,
            'Diagnóstico de prueba ' || i,
            CASE WHEN estado=3 THEN 'Solución aplicada ' || i ELSE NULL END
        );
    END LOOP;
END $$;

-- ================================
-- FIN DEL SCRIPT
-- ================================
