/*
Copyright (c) 2025 Alexander Aviles
Licencia: Creative Commons Attribution-NonCommercial 4.0 International
Prohibido su uso con fines comerciales.
Ver: https://creativecommons.org/licenses/by-nc/4.0/
*/

package com.in.nova.tech.resource;

import com.in.nova.tech.controller.UsuarioBean;
import com.in.nova.tech.dto.ClienteDto;
import com.in.nova.tech.dto.UsuarioDto;
import com.in.nova.tech.entity.Cliente;
import com.in.nova.tech.entity.Ticket;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.tags.Tags;
import com.in.nova.tech.controller.AbstractDataPersistence;
import com.in.nova.tech.controller.ClientesBean;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


@Path("/clientes") // Define la ruta base para los recursos de Cliente
@Tags(value = {
    @Tag(name = "Gestion de Rest Cliente", description = "Operaciones relacionadas con los clientes esto incluye CRUD y gestión de datos."),
})
 // Aplica el filtro de seguridad a todos los métodos de este recurso esto requiere autenticación y autorización tipo Bearer
public class ClienteResource extends AbstractCrudResource<Cliente,ClienteDto,Integer> {

    private static final Logger LOG = Logger.getLogger(ClienteResource.class.getName());

    @Inject
    private ClientesBean clientesBean;
    @Inject
    private UsuarioBean usuarioBean;

    @Override
    protected AbstractDataPersistence<Cliente> getService() {
        return clientesBean;
    }

    @Override
    protected Integer getId(Cliente entity) {
        return entity.getId();
    }

    @Override
    protected void setId(Cliente entity, Integer integer) {
        entity.setId(integer);
    }

    /**
      esto convierte una entidad Cliente a su DTO correspondiente ClienteDto.
        Si la entidad es null, retorna null.
       sin no es null, crea un nuevo ClienteDto, copia los valores de la entidad y lo retorna.
        @param entity La entidad Cliente a convertir.
        @return El DTO ClienteDto resultante, o null si la entidad es null.

     */
    @Override
    protected ClienteDto toDto(Cliente entity) {
        if (entity == null) {
            return null;
        }
        ClienteDto dto = new ClienteDto();
        Ticket ticket = new Ticket();
        List<String> ticketsid = new ArrayList<>();
        List<String> tickets = new ArrayList<>();
        if (entity.getTickets() != null) {
            for (Ticket t : entity.getTickets()) {
                ticketsid.add(t.getId().toString()); // Agrega el ID del ticket como String
            }
            dto.setTicketsId(ticketsid);
        }
       try{
           dto.setUsuarioId(entity.getIdUsuario().getId());
       }catch (Exception ignored){

       }
        dto.setId(entity.getId());
        dto.setCorreo(entity.getCorreo());
        dto.setNombreCompleto(entity.getNombreCompleto());
        dto.setTelefono(entity.getTelefono());
        return dto;
    }

    @Override
    protected Cliente toEntity(ClienteDto dto) {
        if (dto == null) {
            return null;
        }
        Cliente entity = new Cliente();
        entity.setIdUsuario(usuarioBean.findById(dto.getUsuarioId()));
        entity.setId(dto.getId());
        entity.setCorreo(dto.getCorreo());
        entity.setNombreCompleto(dto.getNombreCompleto());
        entity.setTelefono(dto.getTelefono());
        return entity;
    }

    @Override
    @Transactional
    public Response actualizar(Integer id, ClienteDto dto) {
        LOG.info("Iniciando lógica de actualización para cliente ID: " + id);

        // 1. Validar conflicto de correo electrónico
        Cliente clientePorCorreo = clientesBean.findByCorreo(dto.getCorreo());
        if (clientePorCorreo != null && !clientePorCorreo.getId().equals(id)) {
            throw new WebApplicationException("El correo electrónico '" + dto.getCorreo() + "' ya está en uso por otro cliente.", Response.Status.CONFLICT);
        }

        // 2. Cargar la entidad existente de la base de datos
        Cliente entityToUpdate = clientesBean.findById(id);
        if (entityToUpdate == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"No se encontró el cliente con id: " + id + "\"}").build();
        }

        // 3. Actualizar los campos de la entidad con los valores del DTO
        entityToUpdate.setNombreCompleto(dto.getNombreCompleto());
        entityToUpdate.setCorreo(dto.getCorreo());
        entityToUpdate.setTelefono(dto.getTelefono());

        // 4. Persistir la entidad actualizada
        try {
            Cliente updatedEntity = clientesBean.update(entityToUpdate);
            return Response.ok(toDto(updatedEntity)).build();
        } catch (Exception e) {
            LOG.log(java.util.logging.Level.SEVERE, "Error al persistir la actualización", e);
            throw new WebApplicationException("Error interno del servidor al guardar la actualización.", Response.Status.INTERNAL_SERVER_ERROR);
        }
    }


    
}
