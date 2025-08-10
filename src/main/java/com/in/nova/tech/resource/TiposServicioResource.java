package com.in.nova.tech.resource;


import com.in.nova.tech.controller.AbstractDataPersistence;
import com.in.nova.tech.controller.TiposServicioBean;
import com.in.nova.tech.dto.TiposServicioDto;
import com.in.nova.tech.entity.TiposServicio;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.tags.Tags;

@Path("/tipos-servicio")
@Tags(value = {
        @Tag(name = "Tipos de Servicio", description = "Operaciones relacionadas con los tipos de servicio, incluyendo CRUD y gestión de datos.")
})
public class TiposServicioResource extends AbstractCrudResource<TiposServicio, TiposServicioDto,Integer> {

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
}
