package com.in.nova.tech.dto;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class UsuarioDto implements Serializable {

    private String nombreUsuario;
    private String rol="tecnico";
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer id;


    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer idCliente; // ID del cliente asociado

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer idTecnico; // ID del técnico asociado

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String nombreCompleto; // Nombre completo del usuario

    @JsonProperty(access = JsonProperty.Access.READ_WRITE)
    private String contrasena;

}
