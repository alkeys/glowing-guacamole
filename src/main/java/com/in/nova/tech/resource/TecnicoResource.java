/*
Copyright (c) 2025 Alexander Aviles
Licencia: Creative Commons Attribution-NonCommercial 4.0 International
Prohibido su uso con fines comerciales.
Ver: https://creativecommons.org/licenses/by-nc/4.0/
*/
package com.in.nova.tech.resource;

import com.in.nova.tech.controller.AbstractDataPersistence;
import com.in.nova.tech.controller.TecnicoBean;
import com.in.nova.tech.controller.UsuarioBean;
import com.in.nova.tech.dto.TecnicoDto;
import com.in.nova.tech.entity.Tecnico;
import com.in.nova.tech.entity.Usuario;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.tags.Tags;

import java.util.logging.Logger;

@Path("/tecnicos")
@Tags(value = {
    @Tag(name = "Técnicos", description = "Operaciones relacionadas con los técnicos, incluyendo CRUD y gestión de datos.")
})
public class TecnicoResource extends AbstractCrudResource<Tecnico, TecnicoDto, Integer> {

    private static final Logger LOG = Logger.getLogger(TecnicoResource.class.getName());

    @Inject
    TecnicoBean tecnicoBean;

    @Inject
    UsuarioBean usuarioBean; // Inyectar UsuarioBean

    @Override
    protected AbstractDataPersistence<Tecnico> getService() {
        return tecnicoBean;
    }

    @Override
    protected Integer getId(Tecnico entity) {
        return entity.getId();
    }

    @Override
    protected void setId(Tecnico entity, Integer id) {
        entity.setId(id);
    }

    @Override
    protected TecnicoDto toDto(Tecnico entity) {
        TecnicoDto dto = new TecnicoDto();
        if (entity.getIdUsuario() != null) {
            dto.setIdUsuario(entity.getIdUsuario().getId());
        }
        dto.setId(entity.getId());
        dto.setEspecialidad(entity.getEspecialidad());
        dto.setNombreCompleto(entity.getNombreCompleto());
        dto.setActivo(entity.getActivo());
        return dto;
    }

    @Override
    protected Tecnico toEntity(TecnicoDto dto) {
        // Este método sigue siendo útil para la creación, pero no para la actualización.
        Tecnico entity = new Tecnico();
        if (dto.getIdUsuario() != null) {
            Usuario usuario = usuarioBean.findById(dto.getIdUsuario());
            if (usuario == null) {
                throw new WebApplicationException("El usuario con ID " + dto.getIdUsuario() + " no existe.", Response.Status.BAD_REQUEST);
            }
            entity.setIdUsuario(usuario);
        }
        entity.setId(dto.getId());
        entity.setEspecialidad(dto.getEspecialidad());
        entity.setNombreCompleto(dto.getNombreCompleto());
        entity.setActivo(dto.getActivo() != null ? dto.getActivo() : true);
        return entity;
    }

    @Override
    @Transactional
    public Response actualizar(Integer id, TecnicoDto dto) {
        LOG.info("Iniciando lógica de actualización CORRECTA para técnico ID: " + id);

        // 1. Validar que el idUsuario no esté ya asignado a OTRO técnico
        if (dto.getIdUsuario() != null) {
            Tecnico tecnicoConMismoUsuario = tecnicoBean.findByUsuarioId(dto.getIdUsuario());
            if (tecnicoConMismoUsuario != null && !tecnicoConMismoUsuario.getId().equals(id)) {
                throw new WebApplicationException("El usuario con ID " + dto.getIdUsuario() + " ya está asignado a otro técnico (ID: " + tecnicoConMismoUsuario.getId() + ").", Response.Status.CONFLICT);
            }
        }

        // 2. Cargar la entidad existente de la base de datos
        Tecnico entityToUpdate = tecnicoBean.findById(id);
        if (entityToUpdate == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"No se encontró el técnico con id: " + id + "\"}").build();
        }

        // 3. Cargar el usuario a asociar
        Usuario usuarioAsociado = null;
        if (dto.getIdUsuario() != null) {
            usuarioAsociado = usuarioBean.findById(dto.getIdUsuario());
            if (usuarioAsociado == null) {
                throw new WebApplicationException("El usuario a asignar (ID: " + dto.getIdUsuario() + ") no existe.", Response.Status.BAD_REQUEST);
            }
        }

        // 4. Actualizar los campos de la entidad con los valores del DTO
        entityToUpdate.setNombreCompleto(dto.getNombreCompleto());
        entityToUpdate.setEspecialidad(dto.getEspecialidad());
        entityToUpdate.setActivo(dto.getActivo());
        entityToUpdate.setIdUsuario(usuarioAsociado);

        // 5. Persistir la entidad actualizada
        try {
            Tecnico updatedEntity = tecnicoBean.update(entityToUpdate);
            return Response.ok(toDto(updatedEntity)).build();
        } catch (Exception e) {
            LOG.log(java.util.logging.Level.SEVERE, "Error al persistir la actualización del técnico", e);
            throw new WebApplicationException("Error interno del servidor al guardar la actualización.", Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @DELETE
    @Path("/eliminar/{id}")
    @Transactional
    public Response eliminar(@PathParam("id") Integer id) {
        Tecnico entity = tecnicoBean.findById(id);
        if (entity != null && entity.getActivo()) {
            Integer idTecnicoConMenosTickets = tecnicoBean.tecnicoConMenosTickets();
            tecnicoBean.reasignarTodosLosTickets(id, idTecnicoConMenosTickets);
            preDelete(id, false);
            return Response.ok()
                    .entity(String.format("El técnico con ID %d ha sido desactivado y sus tickets reasignados al técnico con menos tickets (ID: %d).", id, idTecnicoConMenosTickets))
                    .build();
        } else {
          return Response.status(Response.Status.NOT_FOUND)
                .entity(String.format("El técnico con ID %d no existe o ya está desactivado.", id))
                .build();
        }
        
    }

    protected void preDelete(Integer idTecnico, Boolean x) {
        Tecnico entity = tecnicoBean.findById(idTecnico);
        if (entity != null) {
            entity.setActivo(x);
            tecnicoBean.update(entity);
        }
    }

    @Transactional
    @GET
    @Path("/conMenosTickets/{especialidad}")
    public Response tecnicosConMenosTickets(@PathParam("especialidad") String especialidad) {
        var tecnicos = tecnicoBean.tecnicosActivosConMenosTickets(especialidad);
        var dtos = tecnicos.stream().map(this::toDto).toList();
        return Response.ok(dtos).build();
    }
}
