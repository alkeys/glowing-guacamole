/*
Copyright (c) 2025 Alexander Aviles
Licencia: Creative Commons Attribution-NonCommercial 4.0 International
Prohibido su uso con fines comerciales.
Ver: https://creativecommons.org/licenses/by-nc/4.0/
*/
package com.in.nova.tech.filter;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Este filtro se encarga de la AUTORIZACIÓN.
 * Se ejecuta después de AuthenticationFilter y verifica si el usuario
 * (ya autenticado) tiene el rol necesario para acceder al recurso.
 */
@Secured
@Provider
@Priority(Priorities.AUTHORIZATION) // Se ejecuta en la fase de autorización
public class SecuredFilter implements ContainerRequestFilter {

    @Context
    private ResourceInfo resourceInfo; // Información sobre el endpoint que se está llamando

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        // 1. Obtener los roles permitidos desde la anotación @Secured del endpoint
        Method method = resourceInfo.getResourceMethod();
        Secured securedAnnotation = method.getAnnotation(Secured.class);

        // Si por alguna razón el método no tiene la anotación, se deniega el acceso
        if (securedAnnotation == null) {
            abortWithForbidden(requestContext, "Acceso denegado al recurso.");
            return;
        }

        Set<String> rolesPermitidos = new HashSet<>(Arrays.asList(securedAnnotation.rolesAllowed()));

        // 2. Obtener el SecurityContext (que fue establecido por AuthenticationFilter)
        SecurityContext securityContext = requestContext.getSecurityContext();

        // 3. Verificar si el usuario tiene alguno de los roles permitidos
        boolean tienePermiso = false;
        for (String rol : rolesPermitidos) {
            if (securityContext.isUserInRole(rol)) {
                tienePermiso = true;
                break;
            }
        }

        // 4. Si no tiene permiso, abortar la petición con un error 403 Forbidden
        if (!tienePermiso) {
            abortWithForbidden(requestContext, "No tienes los permisos necesarios para realizar esta acción.");
        }
    }

    private void abortWithForbidden(ContainerRequestContext requestContext, String message) {
        requestContext.abortWith(
            Response.status(Response.Status.FORBIDDEN)
                  .entity("{\"error\":\"" + message + "\"}")
                  .type("application/json")
                  .build()
        );
    }
}