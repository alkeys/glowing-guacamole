/*
Copyright (c) 2025 Alexander Aviles
Licencia: Creative Commons Attribution-NonCommercial 4.0 International
Prohibido su uso con fines comerciales.
Ver: https://creativecommons.org/licenses/by-nc/4.0/
*/

package com.in.nova.tech.entity;

import com.in.nova.tech.entity.Usuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tecnicos")
public class Tecnico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tecnico", nullable = false)
    private Integer id;

    @Size(max = 100)
    @NotNull
    @Column(name = "nombre_completo", nullable = false, length = 100)
    private String nombreCompleto;

    @Size(max = 50)
    @Column(name = "especialidad", length = 50)
    private String especialidad;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario")
    private Usuario idUsuario;

    /**
     * Devuelve una representación JSON simple del técnico.
     * Se construye manualmente para evitar dependencias en tiempo de ejecución
     * y prevenir recursión con la relación bidireccional.
     */
    public String toJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"id\":").append(id == null ? "null" : id).append(",");
        sb.append("\"nombreCompleto\":")
                .append(nombreCompleto == null ? "null" : "\"" + escapeJson(nombreCompleto) + "\"")
                .append(",");
        sb.append("\"especialidad\":")
                .append(especialidad == null ? "null" : "\"" + escapeJson(especialidad) + "\"")
                .append(",");
        sb.append("\"activo\":").append(activo == null ? "null" : activo);
        sb.append("}");
        return sb.toString();
    }

    private static String escapeJson(String s) {
        if (s == null) return null;
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }
}