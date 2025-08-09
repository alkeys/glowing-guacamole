/*
Copyright (c) 2025 Alexander Aviles
Licencia: Creative Commons Attribution-NonCommercial 4.0 International
Prohibido su uso con fines comerciales.
Ver: https://creativecommons.org/licenses/by-nc/4.0/
*/
package com.in.nova.tech.resource;


import com.in.nova.tech.control.AbstractDataPersistence;
import com.in.nova.tech.control.TecnicoBean;
import com.in.nova.tech.dto.TecnicoDto;
import com.in.nova.tech.entity.Tecnico;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.tags.Tags;

@Path("/tecnicos")
@Tags(value = {
    @Tag(name = "Técnicos", description = "Operaciones relacionadas con los técnicos, incluyendo CRUD y gestión de datos.")
})
public class TecnicoResource extends AbstractCrudResource<Tecnico, TecnicoDto,Integer> {

    @Inject
    TecnicoBean tecnicoBean;

    @Override
    protected AbstractDataPersistence<Tecnico> getService() {
        return tecnicoBean;
    }

    @Override
    protected Integer getId(Tecnico entity) {
        return entity.getId();
    }

    @Override
    protected void setId(Tecnico entity, Integer integer) {

    }

    /**
     * Mapeo entre entidad y DTO
     * @param entity
     * @return DTO
     */
    @Override
    protected TecnicoDto toDto(Tecnico entity) {
        TecnicoDto dto = new TecnicoDto();
        dto.setId(entity.getId());
        dto.setEspecialidad(entity.getEspecialidad());
        dto.setNombreCompleto(entity.getNombreCompleto());
        dto.setActivo(entity.getActivo());
        return dto;
    }


    /**
     * Mapeo entre DTO y entidad
     * @param dto
     * @return Entidad
     */
    @Override
    protected Tecnico toEntity(TecnicoDto dto) {
        Tecnico entity = new Tecnico();
        entity.setId(dto.getId());
        entity.setEspecialidad(dto.getEspecialidad());
        entity.setNombreCompleto(dto.getNombreCompleto());
        entity.setActivo(dto.getActivo() != null ? dto.getActivo() : true); // Por defecto activo
        return entity;
    }



    /**
     * Eliminar un técnico por su ID.
     * Antes de eliminar, verifica si el técnico tiene órdenes de trabajo asociadas.
     * Si tiene órdenes asociadas, realiza un borrado lógico (desactiva el técnico).
     * y todos los trabajos asociados a ese tecnico se quedan al tecnico con menos trabajos asociados
     * Si no tiene órdenes asociadas, elimina físicamente el técnico.
     * @param id El ID del técnico a eliminar.
     * @return Respuesta HTTP indicando el resultado de la operación.
     */
    @Override
    @DELETE
    @Path("/eliminar/{id}")
    @Transactional
    public Response eliminar(@PathParam("id") Integer id) {
        Tecnico entity = tecnicoBean.findById(id);
        if (entity != null && entity.getActivo()) {
            Integer idTecnicoConMenosTickets = tecnicoBean.tecnicoConMenosTickets();
            // Reasigna todos los tickets del técnico actual al técnico con menos tickets
            tecnicoBean.reasignarTodosLosTickets(id, idTecnicoConMenosTickets);
            // Desactiva el técnico actual
            preDelete(id, false);
            return  Response.ok()
                    .entity(String.format("El técnico con ID %d ha sido desactivado y sus tickets reasignados al técnico con menos tickets (ID: %d).", id, idTecnicoConMenosTickets))
                    .build();
        }
        return Response.status(Response.Status.NOT_FOUND)
                .entity(String.format("El técnico con ID %d no existe o ya está desactivado.", id))
                .build();
    }

    /**
     * manejo de borrado logico si se borra un tecnico que tiene ordenes de trabajo asociadas tiene que desactivar el tecnico y no borrarlo fisicamente
     * @param idTecnico
     * @param x si es true  lo activa si es false lo desactiva
     */
    protected void preDelete(Integer idTecnico,Boolean x) {
        Tecnico entity = tecnicoBean.findById(idTecnico);
        if (entity != null) {
            entity.setActivo(x);
            tecnicoBean.update(entity);
        }
    }


    /** metodo para asignar ordenes de trabajo a un tecnicos automaticamente a la que tenga menos ordenes de trabajo
     */




}
