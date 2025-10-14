package com.in.nova.tech.resource;

import java.io.Serializable;

import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.tags.Tags;

import com.in.nova.tech.controller.UsuarioBean;
import com.in.nova.tech.dto.UsuarioDto;
import com.in.nova.tech.entity.Usuario;
import com.in.nova.tech.utils.PasswordHashSeguro;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/login")
@Tags(value = { @Tag(name = "Login", description = "Operación de autenticación de usuarios y generación de tokens JWT.") })
public class LoginResource implements Serializable {

    @Inject
    private UsuarioBean usuarioBean;


    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(UsuarioDto credentials) {
        Usuario usuario = usuarioBean.findByNombreUsuario(credentials.getNombreUsuario());

        if (usuario == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("{\"error\":\"Usuario o contraseña incorrecta\"}").build();
        }

        if (usuario.getRol() == null || usuario.getRol().isEmpty()) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Error Crítico: El rol del usuario no se cargó desde la base de datos.\"}").build();
        }

        if (PasswordHashSeguro.checkPassword(credentials.getContrasena(), usuario.getContrasenaHash())) {
            String tecnicoJson = usuario.getTecnico() != null ? usuario.getTecnico().toJson() : "null";
            String clienteJson = usuario.getCliente() != null ? usuario.getCliente().toJson() : "null";
            String responseJson = String.format(
                "{\"rol_cargado\":\"%s\", \"userId\":%d, \"tecnico\":%s, \"cliente\":%s}",
                 usuario.getRol(), usuario.getId(), tecnicoJson, clienteJson
            );
            return Response.ok(responseJson).build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).entity("{\"error\":\"Usuario o contraseña incorrecta\"}").build();
        }
    }
}