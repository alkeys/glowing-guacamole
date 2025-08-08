/*
Copyright (c) 2025 Alexander Aviles
Licencia: Creative Commons Attribution-NonCommercial 4.0 International
Prohibido su uso con fines comerciales.
Ver: https://creativecommons.org/licenses/by-nc/4.0/
*/

package com.in.nova.tech.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
public class TicketDto implements Serializable {
    private Integer id;

    // --- Campos de Cliente ---
    private Integer idCliente; // ✅ El ID del cliente
    private String nombreCliente; // ✅ El nombre para mostrar en la UI

    // --- Campos de Tipo de Servicio ---
    private Integer idTipoServicio;
    private String nombreTipoServicio;

    // --- Campos de Tecnico ---
    private Integer idTecnico;
    private String nombreTecnico;

    // --- Campos de Estado ---
    private Integer idEstado;
    private String nombreEstado;

    // --- Fechas y datos del Ticket ---
    private LocalDate fechaSolicitud;
    private LocalDate fechaAsignacion;
    private LocalDate fechaCierre;
    private String diagnostico;
    private String solucion;
}