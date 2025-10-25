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
import com.in.nova.tech.utils.PasswordHashSeguro;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.tags.Tags;

import java.util.logging.Logger;

import static com.in.nova.tech.utils.PasswordHashSeguro.hashPassword;


@Path("/usuarios")
@Tags(value = {
        @Tag(name = "Usuarios", description = "Operaciones relacionadas con los usuarios, incluyendo CRUD y gestión de datos.")
})
public class UsuarioResource extends AbstractCrudResource<Usuario, UsuarioDto, Integer> {

    private static final Logger LOG = Logger.getLogger(UsuarioResource.class.getName());

    @Inject
    private UsuarioBean usuarioBean;

    @Override
    protected AbstractDataPersistence<Usuario> getService() {
        return usuarioBean;
    }

    @Override
    protected Integer getId(Usuario entity) {
        return entity.getId();
    }

    @Override
    protected void setId(Usuario entity, Integer id) {
        entity.setId(id);
    }

    @Override
    protected UsuarioDto toDto(Usuario entity) {
        UsuarioDto dto = new UsuarioDto();
        dto.setId(entity.getId());
        dto.setNombreUsuario(entity.getNombreUsuario());
        dto.setRol(entity.getRol());
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
        // Este método ya no es ideal para la actualización, causa el problema de orphanRemoval.
        // La nueva lógica de `actualizar` lo evita.
        Usuario entity = new Usuario();
        entity.setId(dto.getId());
        entity.setNombreUsuario(dto.getNombreUsuario());
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

    @Override
    public Response actualizar(Integer id, UsuarioDto dto) {
        LOG.info("Iniciando lógica de actualización CORRECTA para usuario ID: " + id);

        // 1. Validar conflicto de nombre de usuario
        Usuario existingUserByName = usuarioBean.findByNombreUsuario(dto.getNombreUsuario());
        if (existingUserByName != null && !existingUserByName.getId().equals(id)) {
            throw new WebApplicationException("El nombre de usuario '" + dto.getNombreUsuario() + "' ya está en uso por otro usuario.", Response.Status.CONFLICT);
        }

        // 2. Cargar la entidad existente de la base de datos
        Usuario entityToUpdate = usuarioBean.findById(id);
        if (entityToUpdate == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"No se encontró el usuario con id: " + id + "\"}").build();
        }

        // 3. Actualizar los campos de la entidad con los valores del DTO
        entityToUpdate.setNombreUsuario(dto.getNombreUsuario());
        entityToUpdate.setRol(dto.getRol());

        // 4. Actualizar la contraseña solo si se proporcionó una nueva
        if (dto.getContrasena() != null && !dto.getContrasena().isEmpty() && !dto.getContrasena().equals("string")) {
             if (dto.getContrasena().length() < 4) {
                throw new WebApplicationException("La contraseña debe tener al menos 4 caracteres", Response.Status.BAD_REQUEST);
            }
            if (dto.getContrasena().length() > 64) {
                throw new WebApplicationException("La contraseña no puede tener más de 64 caracteres", Response.Status.BAD_REQUEST);
            }
            String hash = PasswordHashSeguro.hashPassword(dto.getContrasena());
            entityToUpdate.setContrasenaHash(hash);
        }

        // 5. Persistir la entidad actualizada
        try {
            Usuario updatedEntity = usuarioBean.update(entityToUpdate);
            return Response.ok(toDto(updatedEntity)).build();
        } catch (Exception e) {
            LOG.log(java.util.logging.Level.SEVERE, "Error al persistir la actualización", e);
            throw new WebApplicationException("Error interno del servidor al guardar la actualización.", Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
}
