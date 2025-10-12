/*
Copyright (c) 2025 Alexander Aviles
Licencia: Creative Commons Attribution-NonCommercial 4.0 International
Prohibido su uso con fines comerciales.
Ver: https://creativecommons.org/licenses/by-nc/4.0/
*/
package com.in.nova.tech.resource;
import com.in.nova.tech.controller.AbstractDataPersistence;
import com.in.nova.tech.controller.ClientesBean;
import com.in.nova.tech.controller.TecnicoBean;
import com.in.nova.tech.controller.UsuarioBean;
import com.in.nova.tech.dto.UsuarioDto;
import com.in.nova.tech.entity.Usuario;
import com.in.nova.tech.filter.Secured;
import com.in.nova.tech.utils.JwtUtil;
import com.in.nova.tech.utils.PasswordHashSeguro;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.tags.Tags;

import static com.in.nova.tech.utils.PasswordHashSeguro.hashPassword;


@Path("/usuarios")
@Tags(value = {
        @Tag(name = "Usuarios", description = "Operaciones relacionadas con los usuarios, incluyendo CRUD y gestión de datos.")
})
public class UsuarioResource extends AbstractCrudResource<Usuario, UsuarioDto, Integer> {

    @Inject
    private UsuarioBean usuarioBean;

    @Inject 
    private ClientesBean clientesBean;

    @Inject
    private TecnicoBean tecnicoBean;
    
    @Inject
    private JwtUtil jwtUtil;


    @Override
    protected AbstractDataPersistence<Usuario> getService() {
        return usuarioBean;
    }

    @Override
    protected Integer getId(Usuario entity) {
        return entity.getId();
    }

    @Override
    protected void setId(Usuario entity, Integer integer) {
    }

    @Override
    protected UsuarioDto toDto(Usuario entity) {
        UsuarioDto dto = new UsuarioDto();
        dto.setId(entity.getId());
        dto.setNombreUsuario(entity.getNombreUsuario());
        dto.setRol(entity.getRol());
        // Asignar idCliente e idTecnico si existen
        if (entity.getCliente() != null) {
            dto.setIdCliente(entity.getCliente().getId());
        }
        if (entity.getTecnico() != null) {
            dto.setIdTecnico(entity.getTecnico().getId());
        }
        return dto;
    }

    @Override
    protected Usuario toEntity(UsuarioDto dto) {
        Usuario entity = new Usuario();
        entity.setId(dto.getId());
        entity.setNombreUsuario(dto.getNombreUsuario());
        //incrita la contrasenaHash primero
        if (dto.getContrasena() == null || dto.getContrasena().isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía");
        }
        if (dto.getContrasena().length() < 4) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres");
        }
        if (dto.getContrasena().length() > 64) {
            throw new IllegalArgumentException("La contraseña no puede tener más de 64 caracteres");
        }
        String hash = hashPassword(dto.getContrasena());
        entity.setContrasenaHash(hash);
        entity.setRol(dto.getRol());
        return entity;
    }

    @POST
    @Path("/login")
    @Consumes("application/json")
    public Response login(UsuarioDto credentials) {
        Usuario usuario = usuarioBean.findByNombreUsuario(credentials.getNombreUsuario());

        if (usuario == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\":\"Usuario o contraseña incorrecta\"}")
                    .build();
        }

        // Bloque de depuración para verificar el rol
        if (usuario.getRol() == null || usuario.getRol().isEmpty()) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Error Crítico: El rol del usuario no se cargó desde la base de datos.\"}")
                    .build();
        }

        if (PasswordHashSeguro.checkPassword(credentials.getContrasena(), usuario.getContrasenaHash())) {
            String token = jwtUtil.generateToken(usuario);
            String tecnicoJson = usuario.getTecnico() != null ? usuario.getTecnico().toJson() : "null";
            String clienteJson = usuario.getCliente() != null ? usuario.getCliente().toJson() : "null";
            String responseJson = String.format(
                "{\"token\":\"%s\", \"rol_cargado\":\"%s\", \"userId\":%d, \"tecnico\":%s, \"cliente\":%s}",
                token, usuario.getRol(), usuario.getId(), tecnicoJson, clienteJson
            );
            return Response.ok(responseJson).build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\":\"Usuario o contraseña incorrecta\"}")
                    .build();
        }
    }






}
