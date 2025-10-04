/*
Copyright (c) 2025 Alexander Aviles
Licencia: Creative Commons Attribution-NonCommercial 4.0 International
Prohibido su uso con fines comerciales.
Ver: https://creativecommons.org/licenses/by-nc/4.0/
*/
package com.in.nova.tech.resource;
import com.in.nova.tech.controller.AbstractDataPersistence;
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
        try {
            dto.setIdUsuario(entity.getCliente().getIdUsuario().getId());
            dto.setNombreCompleto(entity.getCliente().getNombreCompleto());
        } catch (Exception e) {
            dto.setIdUsuario(null);
            dto.setNombreCompleto(null);
        }
        try {
            dto.setIdTecnico(entity.getTecnico().getId());
            dto.setNombreCompleto(entity.getTecnico().getNombreCompleto());
        } catch (Exception e) {
            dto.setIdTecnico(null);
            dto.setNombreCompleto(null);
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
        if (PasswordHashSeguro.checkPassword(credentials.getContrasena(), usuario.getContrasenaHash())) {
            String token = jwtUtil.generateToken(usuario);
            return Response.ok("{\"token\":\"" + token + "\"}").build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\":\"Usuario o contraseña incorrecta\"}")
                    .build();
        }
    }


    // --- Endpoints Protegidos ---

    @GET
    @Path("/listar")
    @Secured(rolesAllowed = {"administrador"}) // Solo el rol 'administrador' puede listar usuarios
    public Response listAllUsers() {
        // Delega la llamada al método 'listar' de la clase padre.
        return super.listar();
    }

    @GET
    @Path("/obtener/{id}")
    @Secured(rolesAllowed = {"administrador", "cliente", "tecnico"})
    public Response getUserById(@PathParam("id") Integer id) {
        // Delega la llamada al método 'obtenerPorId' de la clase padre.
        return super.obtenerPorId(id);
    }

    @POST
    @Path("/crear")
    @Secured(rolesAllowed = {"administrador"})
    public Response createUser(UsuarioDto dto, @Context UriInfo uriInfo) {
         if (dto.getRol() == null || dto.getRol().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\":\"El rol es obligatorio\"}").build();
        }
        // Delega la llamada al método 'crear' de la clase padre.
        return super.crear(dto, uriInfo);
    }

    @PUT
    @Path("/actualizar/{id}")
    @Secured(rolesAllowed = {"administrador"})
    public Response updateUser(@PathParam("id") Integer id, UsuarioDto dto) {
        // Delega la llamada al método 'actualizar' de la clase padre.
        return super.actualizar(id, dto);
    }

    @DELETE
    @Path("/eliminar/{id}")
    @Secured(rolesAllowed = {"administrador"})
    public Response deleteUser(@PathParam("id") Integer id) {
        // Delega la llamada al método 'eliminar' de la clase padre.
        return super.eliminar(id);
    }



   //esto es para probar el filtro de seguridad
 
    @Override
    @GET
    @Secured(rolesAllowed = {"administrador"})
    public Response listar() {
        return super.listar();
    }

    @Override
    @POST
    @Secured(rolesAllowed = {"administrador"})
    public Response crear(UsuarioDto dto, @Context UriInfo uriInfo) {
        return super.crear(dto, uriInfo);
    }
    
    @Override
    @PUT
    @Path("/actualizar/{id}")
    @Secured(rolesAllowed = {"administrador"})
    public Response actualizar(@PathParam("id") Integer id, UsuarioDto dto) {
        return super.actualizar(id, dto);
    }

    @Override
    @DELETE
    @Path("/eliminar/{id}")
    @Secured(rolesAllowed = {"administrador"})
    public Response eliminar(@PathParam("id") Integer id) {
        return super.eliminar(id);
    }   



}
