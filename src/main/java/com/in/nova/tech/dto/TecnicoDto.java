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
import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@Setter
public class TecnicoDto implements Serializable {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer id;
    private String nombreCompleto;
    private String especialidad;
    private Boolean activo = true;

}
