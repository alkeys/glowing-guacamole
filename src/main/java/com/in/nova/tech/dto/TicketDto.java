/*
Copyright (c) 2025 Alexander Aviles
Licencia: Creative Commons Attribution-NonCommercial 4.0 International
Prohibido su uso con fines comerciales.
Ver: https://creativecommons.org/licenses/by-nc/4.0/
*/

package com.in.nova.tech.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;


/**
 * DTO para representar un Ticket en la UI.
 * Incluye campos de Cliente, Tipo de Servicio, Técnico y Estado.
 * Los campos de ID son editables, mientras que los nombres son de solo lectura.
 */

@Getter
@Setter
public class TicketDto implements Serializable {
    private Integer id;

    // --- Campos de Cliente ---
    private Integer idCliente; // ✅ El ID del cliente
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String nombreCliente; // ✅ El nombre para mostrar en la UI

    // --- Campos de Tipo de Servicio ---
    private Integer idTipoServicio;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String nombreTipoServicio;

    // --- Campos de Tecnico ---
    private Integer idTecnico;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String nombreTecnico;

    // --- Campos de Estado ---
    private Integer idEstado;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String nombreEstado;

    // --- Fechas y datos del Ticket ---
    private LocalDate fechaSolicitud;
    private LocalDate fechaAsignacion;
    private LocalDate fechaCierre;
    private String diagnostico;
    private String solucion;
}