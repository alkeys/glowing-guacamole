/*
Copyright (c) 2025 Alexander Aviles
Licencia: Creative Commons Attribution-NonCommercial 4.0 International
Prohibido su uso con fines comerciales.
Ver: https://creativecommons.org/licenses/by-nc/4.0/
*/

package com.in.nova.tech.resource;

import com.in.nova.tech.controller.*;
import com.in.nova.tech.dto.TicketDto;
import com.in.nova.tech.entity.*;


import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.tags.Tags;

import java.util.logging.Logger;


@Path("/tickets")
@Tags(value = {
    @Tag(name = "Gestion de Rest Ticket", description = "Operaciones relacionadas con los tickets, incluyendo CRUD y gestión de datos.")
})
public class TicketResource extends AbstractCrudResource<Ticket, TicketDto, Integer> {

    private static final Logger LOG = Logger.getLogger(TicketResource.class.getName());

   @Inject
    private TicketBean ticketBean;
    @Inject
    private ClientesBean clientesBean;
    @Inject
    private TiposServicioBean tiposServicioBean;
    @Inject
    private EstadosTicketBean estadosTicketBean;
    @Inject
    private TecnicoBean tecnicosBean;


    @Override
    protected AbstractDataPersistence<Ticket> getService() {
        return ticketBean;
    }

    @Override
    protected Integer getId(Ticket entity) {
        return entity.getId();
    }

    @Override
    protected void setId(Ticket entity, Integer integer) {
        entity.setId(integer);
    }



    @Override
    protected TicketDto toDto(Ticket ticket) {
        if (ticket == null) return null;

        TicketDto dto = new TicketDto();
        dto.setId(ticket.getId());

        // Mapear datos del Cliente
        if (ticket.getIdCliente() != null) {
            dto.setIdCliente(ticket.getIdCliente().getId());
            dto.setNombreCliente(ticket.getIdCliente().getNombreCompleto());
        }

        // Mapear datos del Tipo de Servicio
        if (ticket.getIdTipoServicio() != null) {
            dto.setIdTipoServicio(ticket.getIdTipoServicio().getId());
            dto.setNombreTipoServicio(ticket.getIdTipoServicio().getNombreTipo()); // Asumiendo que el campo se llama 'nombre'
        }

        // Mapear datos del Tecnico
        if (ticket.getIdTecnico() != null) {
            dto.setIdTecnico(ticket.getIdTecnico().getId());
            dto.setNombreTecnico(ticket.getIdTecnico().getNombreCompleto()); // Asumiendo que el campo se llama 'nombreCompleto'
        }

        // Mapear datos del Estado
        if (ticket.getIdEstado() != null) {
            dto.setIdEstado(ticket.getIdEstado().getId());
            dto.setNombreEstado(ticket.getIdEstado().getNombreEstado()); // Asumiendo que el campo se llama 'nombre'
        }

        // Mapear el resto de los campos
        dto.setFechaSolicitud(ticket.getFechaSolicitud());
        dto.setFechaAsignacion(ticket.getFechaAsignacion());
        dto.setFechaCierre(ticket.getFechaCierre());
        dto.setDiagnostico(ticket.getDiagnostico());
        dto.setSolucion(ticket.getSolucion());

        return dto;
    }



    /**
     * Convierte un TicketDto a una entidad Ticket.
     * Este método es fundamental para las operaciones de CREAR y ACTUALIZAR.
     * como función de mapeo entre DTO y entidad.
     * * @param dto El TicketDto a convertir.
     */
    @Override
    protected Ticket toEntity(TicketDto dto) {
        if (dto == null) return null;

        Ticket ticket = new Ticket();

        // 1. Mapea los campos directos del DTO a la entidad
        ticket.setId(dto.getId()); // El ID será nulo en una creación, y tendrá valor en una actualización
        ticket.setFechaSolicitud(dto.getFechaSolicitud());
        ticket.setFechaAsignacion(dto.getFechaAsignacion());
        ticket.setFechaCierre(dto.getFechaCierre());
        ticket.setDiagnostico(dto.getDiagnostico());
        ticket.setSolucion(dto.getSolucion());

        // 2. Busca las entidades relacionadas usando los IDs del DTO

        // Busca el Cliente
        if (dto.getIdCliente() != null) {
            Cliente cliente = clientesBean.findById(dto.getIdCliente());
            // Aquí podrías añadir validación para asegurar que 'cliente' no es nulo
            ticket.setIdCliente(cliente);
        }

        // Busca el Tipo de Servicio
        if (dto.getIdTipoServicio() != null) {
            TiposServicio tipoServicio = tiposServicioBean.findById(dto.getIdTipoServicio());
            ticket.setIdTipoServicio(tipoServicio);
        }

        // Busca el Técnico
        if (dto.getIdTecnico() != null) {
            Tecnico tecnico = tecnicosBean.findById(dto.getIdTecnico());
            ticket.setIdTecnico(tecnico);
        }

        // Busca el Estado
        if (dto.getIdEstado() != null) {
            EstadosTicket estado = estadosTicketBean.findById(dto.getIdEstado());
            ticket.setIdEstado(estado);
        }

        return ticket;
    }

    @Override
    @Transactional
    public Response actualizar(Integer id, TicketDto dto) {
        LOG.info("Iniciando lógica de actualización para ticket ID: " + id);

        // 1. Cargar la entidad existente de la base de datos
        Ticket entityToUpdate = ticketBean.findById(id);
        if (entityToUpdate == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"No se encontró el ticket con id: " + id + "\"}").build();
        }

        // 2. Actualizar los campos y relaciones de la entidad con los valores del DTO
        if (dto.getIdCliente() != null) {
            Cliente cliente = clientesBean.findById(dto.getIdCliente());
            if (cliente == null) {
                throw new WebApplicationException("El cliente con ID " + dto.getIdCliente() + " no existe.", Response.Status.BAD_REQUEST);
            }
            entityToUpdate.setIdCliente(cliente);
        }

        if (dto.getIdTipoServicio() != null) {
            TiposServicio tipoServicio = tiposServicioBean.findById(dto.getIdTipoServicio());
            if (tipoServicio == null) {
                throw new WebApplicationException("El tipo de servicio con ID " + dto.getIdTipoServicio() + " no existe.", Response.Status.BAD_REQUEST);
            }
            entityToUpdate.setIdTipoServicio(tipoServicio);
        }

        if (dto.getIdTecnico() != null) {
            Tecnico tecnico = tecnicosBean.findById(dto.getIdTecnico());
            if (tecnico == null) {
                throw new WebApplicationException("El técnico con ID " + dto.getIdTecnico() + " no existe.", Response.Status.BAD_REQUEST);
            }
            entityToUpdate.setIdTecnico(tecnico);
        } else {
            entityToUpdate.setIdTecnico(null);
        }

        if (dto.getIdEstado() != null) {
            EstadosTicket estado = estadosTicketBean.findById(dto.getIdEstado());
            if (estado == null) {
                throw new WebApplicationException("El estado del ticket con ID " + dto.getIdEstado() + " no existe.", Response.Status.BAD_REQUEST);
            }
            entityToUpdate.setIdEstado(estado);
        }

        entityToUpdate.setFechaSolicitud(dto.getFechaSolicitud());
        entityToUpdate.setFechaAsignacion(dto.getFechaAsignacion());
        entityToUpdate.setFechaCierre(dto.getFechaCierre());
        entityToUpdate.setDiagnostico(dto.getDiagnostico());
        entityToUpdate.setSolucion(dto.getSolucion());

        // 3. Persistir la entidad actualizada
        try {
            Ticket updatedEntity = ticketBean.update(entityToUpdate);
            return Response.ok(toDto(updatedEntity)).build();
        } catch (Exception e) {
            LOG.log(java.util.logging.Level.SEVERE, "Error al persistir la actualización del ticket", e);
            throw new WebApplicationException("Error interno del servidor al guardar la actualización.", Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
}
