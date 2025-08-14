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
import java.util.List;
import java.util.Map;

/**
 * DTO para representar un Cliente en la UI.
 * Incluye campos básicos como ID, nombre completo, correo y teléfono.
 */
@Getter
@Setter
public class ClienteDto implements Serializable {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer id;
    private String nombreCompleto;
    private String correo;
    private String telefono;


    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<String> TicketsId;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    //lista de tickets asociados al cliente datos solo de lectura
    //datos basicos del ticket id,diagnostico
    private Map<String,String> tickets;



}
