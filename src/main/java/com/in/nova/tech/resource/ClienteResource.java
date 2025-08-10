/*
Copyright (c) 2025 Alexander Aviles
Licencia: Creative Commons Attribution-NonCommercial 4.0 International
Prohibido su uso con fines comerciales.
Ver: https://creativecommons.org/licenses/by-nc/4.0/
*/

package com.in.nova.tech.resource;

import com.in.nova.tech.dto.ClienteDto;
import com.in.nova.tech.entity.Cliente;
import com.in.nova.tech.entity.Ticket;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.tags.Tags;
import com.in.nova.tech.controller.AbstractDataPersistence;
import com.in.nova.tech.controller.ClientesBean;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.List;


@Path("/clientes") // Define la ruta base para los recursos de Cliente
@Tags(value = {
    @Tag(name = "Gestion de Rest Cliente", description = "Operaciones relacionadas con los clientes esto incluye CRUD y gestión de datos."),
})
public class ClienteResource extends AbstractCrudResource<Cliente,ClienteDto,Integer> {

    @Inject
    private ClientesBean clientesBean;

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
        List<String> tickets = new ArrayList<>();
        if (entity.getTickets() != null) {
            for (Ticket t : entity.getTickets()) {
                tickets.add(t.getId().toString()); // Agrega el ID del ticket como String
            }
            dto.setIdtickets(tickets); // Asigna la lista de IDs al DTO
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
        entity.setId(dto.getId());
        entity.setCorreo(dto.getCorreo());
        entity.setNombreCompleto(dto.getNombreCompleto());
        entity.setTelefono(dto.getTelefono());
        return entity;
    }


}
