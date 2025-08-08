/*
Copyright (c) 2025 Alexander Aviles
Licencia: Creative Commons Attribution-NonCommercial 4.0 International
Prohibido su uso con fines comerciales.
Ver: https://creativecommons.org/licenses/by-nc/4.0/
*/

package com.in.nova.tech.resource;

import com.in.nova.tech.control.AbstractDataPersistence;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Recurso REST abstracto y genérico para operaciones CRUD usando Entidades y DTOs.
 * @param <T>  El tipo de la entidad (ej. Cliente).
 * @param <D>  El tipo del DTO (ej. ClienteDto).
 * @param <ID> El tipo del ID de la entidad (ej. Integer, Long).
 */
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public abstract class AbstractCrudResource<T, D, ID> {

    private static final Logger LOG = Logger.getLogger(AbstractCrudResource.class.getName());

    // MÉTODOS ABSTRACTOS A IMPLEMENTAR POR LAS CLASES HIJAS

    /**
     * Proporciona el servicio de persistencia (DAO) para la entidad T.
     * @return Una instancia de AbstractDataPersistence<T>.
     */
    protected abstract AbstractDataPersistence<T> getService();

    /**
     * Obtiene el ID de una entidad.
     * @param entity la entidad.
     * @return el ID de la entidad.
     */
    protected abstract ID getId(T entity);

    /**
     * Establece el ID en una entidad (usado en la actualización).
     * @param entity la entidad.
     * @param id el id a establecer.
     */
    protected abstract void setId(T entity, ID id);

    /**
     * Convierte una entidad (T) a su DTO correspondiente (D).
     * @param entity La entidad a convertir.
     * @return El DTO resultante.
     */
    protected abstract D toDto(T entity);

    /**
     * Convierte un DTO (D) a su entidad correspondiente (T).
     * @param dto El DTO a convertir.
     * @return La entidad resultante.
     */
    protected abstract T toEntity(D dto);


    // MÉTODOS CRUD GENÉRICOS

    @GET
    @Path("/listar")
    public Response listar() {
        try {
            List<D> dtos = getService().findAll()
                    .stream()
                    .map(this::toDto) // Convierte cada entidad a DTO
                    .collect(Collectors.toList());
            return Response.ok(dtos).build();
        } catch (Exception e) {
            return buildErrorResponse(e, "Error interno al listar entidades");
        }
    }

    @GET
    @Path("/obtener/{id}")
    public Response obtenerPorId(@PathParam("id") ID id) {
        if (id == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\":\"ID no puede ser nulo\"}").build();
        }
        try {
            T entity = getService().findById(id);
            if (entity != null) {
                return Response.ok(toDto(entity)).build(); // Devuelve el DTO
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"No se encontró una entidad con id: " + id + "\"}").build();
            }
        } catch (Exception e) {
            return buildErrorResponse(e, "Error interno al obtener la entidad");
        }
    }

    @POST
    @Path("/crear")
    @Transactional
    public Response crear(D dto, @Context UriInfo uriInfo) { // Recibe un DTO
        if (dto == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\":\"El DTO no puede ser nulo\"}").build();
        }
        try {
            T entity = toEntity(dto); // Convierte DTO a Entidad
            getService().create(entity);
            ID entityId = getId(entity);

            UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder().path(entityId.toString());
            return Response.created(uriBuilder.build()).entity(toDto(entity)).build(); // Devuelve el DTO creado
        } catch (Exception e) {
            return buildErrorResponse(e, "Error interno al crear la entidad");
        }
    }

    @PUT
    @Path("/actualizar/{id}")
    @Transactional
    public Response actualizar(@PathParam("id") ID id, D dto) { // Recibe un DTO
        if (id == null || dto == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\":\"ID y DTO no pueden ser nulos\"}").build();
        }
        try {
            if (getService().findById(id) == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"No se encontró entidad con id: " + id + "\"}").build();
            }
            T entity = toEntity(dto); // Convierte DTO a Entidad
            setId(entity, id); // Asegura que la entidad tenga el ID correcto
            T updatedEntity = getService().update(entity);
            return Response.ok(toDto(updatedEntity)).build(); // Devuelve el DTO actualizado
        } catch (Exception e) {
            return buildErrorResponse(e, "Error interno al actualizar la entidad");
        }
    }

    @DELETE
    @Path("/eliminar/{id}")
    @Transactional
    public Response eliminar(@PathParam("id") ID id) {
        if (id == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\":\"ID no puede ser nulo\"}").build();
        }
        try {
            T entity = getService().findById(id);
            if (entity == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"No se encontró entidad con id: " + id + "\"}").build();
            }
            getService().delete(entity);
            return Response.noContent().build();
        } catch (Exception e) {
            return buildErrorResponse(e, "Error interno al eliminar la entidad");
        }
    }

    @GET
    @Path("/listar/rango")
    public Response listarPorRango(@QueryParam("start") @DefaultValue("0") int start,
                                   @QueryParam("size") @DefaultValue("50") int size) {
        try {
            if (start < 0 || size <= 0) {
                return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\":\"Parámetros de paginación inválidos\"}").build();
            }
            List<D> dtos = getService().findRange(start, size)
                    .stream()
                    .map(this::toDto) // Convierte cada entidad a DTO
                    .collect(Collectors.toList());
            long total = getService().count(); // count() debería devolver long
            return Response.ok(dtos).header("Total-Records", total).build();
        } catch (Exception e) {
            return buildErrorResponse(e, "Error interno al listar por rango");
        }
    }

    // UTILIDADES

    private Throwable getRootCause(Throwable e) {
        Throwable cause = e;
        while (cause.getCause() != null && cause != cause.getCause()) {
            cause = cause.getCause();
        }
        return cause;
    }

    private Response buildErrorResponse(Exception e, String defaultMessage) {
        Throwable rootCause = getRootCause(e);
        LOG.log(Level.SEVERE, defaultMessage + ": " + rootCause.getClass().getName() + " - " + rootCause.getMessage(), rootCause);

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", defaultMessage);
        errorResponse.put("exception", rootCause.getClass().getName());
        errorResponse.put("message", rootCause.getMessage());

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(errorResponse)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}