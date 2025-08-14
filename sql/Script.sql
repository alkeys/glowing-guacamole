-- ================================
-- CREACIÓN DE TABLAS NORMALIZADAS
-- ================================

-- Tabla de usuarios, debe crearse primero para que otras tablas puedan referenciarla
CREATE TABLE usuarios (
    id_usuario SERIAL PRIMARY KEY,
    nombre_usuario VARCHAR(50) UNIQUE NOT NULL,
    contrasena_hash VARCHAR(255) NOT NULL,
    rol VARCHAR(20) NOT NULL CHECK (rol IN ('cliente', 'tecnico', 'administrador'))
);

-- Tabla de clientes
CREATE TABLE clientes (
    id_cliente SERIAL PRIMARY KEY,
    id_usuario INTEGER UNIQUE REFERENCES usuarios(id_usuario),
    nombre_completo VARCHAR(100) NOT NULL,
    correo VARCHAR(100),
    telefono VARCHAR(20)
);

-- Tabla de técnicos
CREATE TABLE tecnicos (
    id_tecnico SERIAL PRIMARY KEY,
    id_usuario INTEGER UNIQUE REFERENCES usuarios(id_usuario),
    nombre_completo VARCHAR(100) NOT NULL,
    especialidad VARCHAR(50), -- ejemplo: hardware, redes, software
    activo BOOLEAN DEFAULT TRUE
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