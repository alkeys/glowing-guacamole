/*
Copyright (c) 2025 Alexander Aviles
Licencia: Creative Commons Attribution-NonCommercial 4.0 International
Prohibido su uso con fines comerciales.
Ver: https://creativecommons.org/licenses/by-nc/4.0/
*/
package com.in.nova.tech.resource;


import com.in.nova.tech.controller.AbstractDataPersistence;
import com.in.nova.tech.controller.EstadosTicketBean;
import com.in.nova.tech.dto.EstadosTicketDto;
import com.in.nova.tech.entity.EstadosTicket;
import com.in.nova.tech.filter.Secured;

import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.tags.Tags;

@Path("/estados-ticket")
@Tags(value = {
        @Tag(name = "Estados de Ticket", description = "Operaciones relacionadas con los estados de los tickets, incluyendo CRUD y gestión de datos.")
})
@Secured
public class EstadosTicketResource extends AbstractCrudResource<EstadosTicket, EstadosTicketDto,Integer> {

    @Inject
    private EstadosTicketBean estadosTicketBean;

    @Override
    protected AbstractDataPersistence<EstadosTicket> getService() {
        return estadosTicketBean;
    }

    @Override
    protected Integer getId(EstadosTicket entity) {
        return entity.getId();
    }

    @Override
    protected void setId(EstadosTicket entity, Integer integer) {

    }

    /**
     * Convierte una entidad (T) a su DTO correspondiente (D).
     * @param entity La entidad a convertir.
     * @return El DTO resultante.
     */
    @Override
    protected EstadosTicketDto toDto(EstadosTicket entity) {
        if (entity == null) {
            return null;
        }
        EstadosTicketDto dto = new EstadosTicketDto();
        dto.setId(entity.getId());
        dto.setNombreEstado(entity.getNombreEstado());
        return dto;
    }

    /**
     * Convierte un DTO (D) a su entidad correspondiente (T).
     * @param dto El DTO a convertir.
     * @return La entidad resultante.
     */
    @Override
    protected EstadosTicket toEntity(EstadosTicketDto dto) {
        if (dto == null) {
            return null;
        }
        EstadosTicket entity = new EstadosTicket();
        entity.setId(dto.getId());
        entity.setNombreEstado(dto.getNombreEstado());
        return entity;
    }
}
