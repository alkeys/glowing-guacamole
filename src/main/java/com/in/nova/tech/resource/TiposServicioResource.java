package com.in.nova.tech.resource;


import com.in.nova.tech.controller.AbstractDataPersistence;
import com.in.nova.tech.controller.TiposServicioBean;
import com.in.nova.tech.dto.TiposServicioDto;
import com.in.nova.tech.entity.TiposServicio;


import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.tags.Tags;

import java.util.logging.Logger;

@Path("/tipos-servicio")
@Tags(value = {
        @Tag(name = "Tipos de Servicio", description = "Operaciones relacionadas con los tipos de servicio, incluyendo CRUD y gestión de datos.")
})

public class TiposServicioResource extends AbstractCrudResource<TiposServicio, TiposServicioDto,Integer> {

    private static final Logger LOG = Logger.getLogger(TiposServicioResource.class.getName());

    @Inject
    private TiposServicioBean tiposServicioBean;

    @Override
    protected AbstractDataPersistence<TiposServicio> getService() {
        return tiposServicioBean;
    }

    @Override
    protected Integer getId(TiposServicio entity) {
        return entity.getId();
    }

    @Override
    protected void setId(TiposServicio entity, Integer integer) {

    }

    @Override
    protected TiposServicioDto toDto(TiposServicio entity) {
        if (entity == null) {
            return null;
        }
        TiposServicioDto dto = new TiposServicioDto();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombreTipo());
        return dto;
    }

    @Override
    protected TiposServicio toEntity(TiposServicioDto dto) {
        if (dto == null) {
            return null;
        }
        TiposServicio entity = new TiposServicio();
        entity.setId(dto.getId());
        entity.setNombreTipo(dto.getNombre());
        return entity;
    }

    @Override
    @Transactional
    public Response actualizar(Integer id, TiposServicioDto dto) {
        LOG.info("Iniciando lógica de actualización para tipo de servicio ID: " + id);

        // 1. Cargar la entidad existente de la base de datos
        TiposServicio entityToUpdate = tiposServicioBean.findById(id);
        if (entityToUpdate == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"No se encontró el tipo de servicio con id: " + id + "\"}").build();
        }

        // 2. Actualizar los campos de la entidad con los valores del DTO
        entityToUpdate.setNombreTipo(dto.getNombre());

        // 3. Persistir la entidad actualizada
        try {
            TiposServicio updatedEntity = tiposServicioBean.update(entityToUpdate);
            return Response.ok(toDto(updatedEntity)).build();
        } catch (Exception e) {
            LOG.log(java.util.logging.Level.SEVERE, "Error al persistir la actualización", e);
            throw new WebApplicationException("Error interno del servidor al guardar la actualización.", Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
}
