-- ==========================
-- DATOS DE PRUEBA COMPLETOS
-- ==========================

-- Clientes (30 registros)
INSERT INTO clientes (nombre_completo, correo, telefono) VALUES
                                                             ('Carlos Ramírez', 'carlos.ramirez@example.com', '7001-0001'),
                                                             ('María González', 'maria.gonzalez@example.com', '7001-0002'),
                                                             ('José Martínez', 'jose.martinez@example.com', '7001-0003'),
                                                             ('Ana López', 'ana.lopez@example.com', '7001-0004'),
                                                             ('Luis Hernández', 'luis.hernandez@example.com', '7001-0005'),
                                                             ('Patricia Morales', 'patricia.morales@example.com', '7001-0006'),
                                                             ('Fernando Castillo', 'fernando.castillo@example.com', '7001-0007'),
                                                             ('Gabriela Pérez', 'gabriela.perez@example.com', '7001-0008'),
                                                             ('Miguel Torres', 'miguel.torres@example.com', '7001-0009'),
                                                             ('Verónica Rivera', 'veronica.rivera@example.com', '7001-0010'),
                                                             ('Raúl Vargas', 'raul.vargas@example.com', '7001-0011'),
                                                             ('Claudia Cruz', 'claudia.cruz@example.com', '7001-0012'),
                                                             ('Jorge Reyes', 'jorge.reyes@example.com', '7001-0013'),
                                                             ('Sofía Jiménez', 'sofia.jimenez@example.com', '7001-0014'),
                                                             ('Daniel Flores', 'daniel.flores@example.com', '7001-0015'),
                                                             ('Mónica Chávez', 'monica.chavez@example.com', '7001-0016'),
                                                             ('Ricardo Navarro', 'ricardo.navarro@example.com', '7001-0017'),
                                                             ('Laura Ortiz', 'laura.ortiz@example.com', '7001-0018'),
                                                             ('Andrés Herrera', 'andres.herrera@example.com', '7001-0019'),
                                                             ('Natalia Ramos', 'natalia.ramos@example.com', '7001-0020'),
                                                             ('Héctor Soto', 'hector.soto@example.com', '7001-0021'),
                                                             ('Paola Delgado', 'paola.delgado@example.com', '7001-0022'),
                                                             ('Felipe Molina', 'felipe.molina@example.com', '7001-0023'),
                                                             ('Lucía Aguilar', 'lucia.aguilar@example.com', '7001-0024'),
                                                             ('Emilio Cordero', 'emilio.cordero@example.com', '7001-0025'),
                                                             ('Silvia Peña', 'silvia.pena@example.com', '7001-0026'),
                                                             ('Francisco Cabrera', 'francisco.cabrera@example.com', '7001-0027'),
                                                             ('Isabel Meza', 'isabel.meza@example.com', '7001-0028'),
                                                             ('Diego Campos', 'diego.campos@example.com', '7001-0029'),
                                                             ('Valeria Pineda', 'valeria.pineda@example.com', '7001-0030');

-- Técnicos (6 registros)
INSERT INTO tecnicos (nombre_completo, especialidad, activo) VALUES
                                                                 ('Luis Torres', 'Hardware', TRUE),
                                                                 ('Ana Pérez', 'Software', TRUE),
                                                                 ('Carlos Martínez', 'Redes', TRUE),
                                                                 ('Marcos López', 'Hardware', TRUE),
                                                                 ('Fernanda Castillo', 'Software', TRUE),
                                                                 ('Ricardo Gómez', 'Redes', TRUE);

-- Tipos de servicio (3 registros)
INSERT INTO tipos_servicio (nombre_tipo) VALUES
                                             ('Hardware'),
                                             ('Software'),
                                             ('Redes');

-- Estados de ticket (3 registros)
INSERT INTO estados_ticket (nombre_estado) VALUES
                                               ('Abierto'),
                                               ('En proceso'),
                                               ('Cerrado');

-- Tickets (50 registros aleatorios)
INSERT INTO tickets (id_cliente, id_tipo_servicio, fecha_solicitud, id_tecnico, id_estado, fecha_asignacion, fecha_cierre, diagnostico, solucion) VALUES
                                                                                                                                                      (1, 1, '2025-07-01', 1, 2, '2025-07-02', NULL, 'Fuente de poder defectuosa', 'Reemplazo pendiente'),
                                                                                                                                                      (2, 2, '2025-07-03', 2, 3, '2025-07-04', '2025-07-05', 'Error de sistema operativo', 'Reinstalación completa'),
                                                                                                                                                      (3, 3, '2025-07-05', 3, 1, NULL, NULL, 'Interrupciones de red', NULL),
                                                                                                                                                      (4, 1, '2025-07-06', 4, 2, '2025-07-07', NULL, 'Placa madre dañada', 'Pendiente de repuesto'),
                                                                                                                                                      (5, 2, '2025-07-07', 5, 3, '2025-07-08', '2025-07-09', 'Fallo en base de datos', 'Optimización y respaldo'),
                                                                                                                                                      (6, 3, '2025-07-09', 6, 1, NULL, NULL, 'Pérdida de conectividad', NULL),
                                                                                                                                                      (7, 1, '2025-07-10', 1, 3, '2025-07-11', '2025-07-12', 'Problema en ventilador', 'Cambio de ventilador'),
                                                                                                                                                      (8, 2, '2025-07-12', 2, 2, '2025-07-13', NULL, 'Aplicación no abre', 'Revisión de permisos'),
                                                                                                                                                      (9, 3, '2025-07-14', 3, 3, '2025-07-15', '2025-07-16', 'Cableado defectuoso', 'Reemplazo de cables'),
                                                                                                                                                      (10, 1, '2025-07-15', 4, 1, NULL, NULL, 'Pantalla rota', NULL),
                                                                                                                                                      (11, 2, '2025-07-16', 5, 3, '2025-07-17', '2025-07-18', 'Virus detectado', 'Eliminación de malware'),
                                                                                                                                                      (12, 3, '2025-07-18', 6, 2, '2025-07-19', NULL, 'Red intermitente', 'Ajuste de configuración'),
                                                                                                                                                      (13, 1, '2025-07-19', 1, 3, '2025-07-20', '2025-07-21', 'Teclado dañado', 'Reemplazo de teclado'),
                                                                                                                                                      (14, 2, '2025-07-21', 2, 1, NULL, NULL, 'Error en aplicación contable', NULL),
                                                                                                                                                      (15, 3, '2025-07-22', 3, 3, '2025-07-23', '2025-07-24', 'Pérdida de señal Wi-Fi', 'Instalación de nuevo router'),
                                                                                                                                                      (16, 1, '2025-07-23', 4, 2, '2025-07-24', NULL, 'Problema con batería', 'Sustitución en curso'),
                                                                                                                                                      (17, 2, '2025-07-25', 5, 3, '2025-07-26', '2025-07-27', 'Sistema lento', 'Optimización de recursos'),
                                                                                                                                                      (18, 3, '2025-07-27', 6, 1, NULL, NULL, 'Conexión inestable', NULL),
                                                                                                                                                      (19, 1, '2025-07-28', 1, 3, '2025-07-29', '2025-07-30', 'Sobrecalentamiento', 'Cambio de pasta térmica'),
                                                                                                                                                      (20, 2, '2025-07-29', 2, 2, '2025-07-30', NULL, 'Error en inicio de sesión', 'Reinicio de credenciales'),
                                                                                                                                                      (21, 3, '2025-07-30', 3, 3, '2025-07-31', '2025-08-01', 'Pérdida total de red', 'Reemplazo de switch'),
                                                                                                                                                      (22, 1, '2025-07-31', 4, 1, NULL, NULL, 'Problema en tarjeta gráfica', NULL),
                                                                                                                                                      (23, 2, '2025-08-01', 5, 3, '2025-08-02', '2025-08-03', 'Error de compatibilidad', 'Actualización de software'),
                                                                                                                                                      (24, 3, '2025-08-02', 6, 2, '2025-08-03', NULL, 'Caída de red parcial', 'Pendiente de revisión'),
                                                                                                                                                      (25, 1, '2025-08-03', 1, 3, '2025-08-04', '2025-08-05', 'Fuente de poder defectuosa', 'Reemplazo completo'),
                                                                                                                                                      (26, 2, '2025-08-04', 2, 1, NULL, NULL, 'Aplicación bloqueada', NULL),
                                                                                                                                                      (27, 3, '2025-08-05', 3, 3, '2025-08-06', '2025-08-07', 'Cable Ethernet roto', 'Sustitución de cable'),
                                                                                                                                                      (28, 1, '2025-08-06', 4, 2, '2025-08-07', NULL, 'Problema en BIOS', 'Actualización en curso'),
                                                                                                                                                      (29, 2, '2025-08-07', 5, 3, '2025-08-08', '2025-08-09', 'Pantallazo azul', 'Reinstalación de controladores'),
                                                                                                                                                      (30, 3, '2025-08-08', 6, 1, NULL, NULL, 'Latencia elevada', NULL);
