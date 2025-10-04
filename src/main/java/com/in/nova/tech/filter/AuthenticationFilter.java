/*
Copyright (c) 2025 Alexander Aviles
Licencia: Creative Commons Attribution-NonCommercial 4.0 International
Prohibido su uso con fines comerciales.
Ver: https://creativecommons.org/licenses/by-nc/4.0/
*/
package com.in.nova.tech.filter;

import com.in.nova.tech.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Provider
@Secured
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

    private static final Logger LOGGER = Logger.getLogger(AuthenticationFilter.class.getName());

    @Inject
    private JwtUtil jwtUtil;

    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            abortWithUnauthorized(requestContext, "No se proporcionó un token de autenticación.");
            return;
        }

        String token = authorizationHeader.substring("Bearer ".length()).trim();

        try {
            Claims claims = jwtUtil.validateToken(token);
            String rol = claims.get("role", String.class);

            if (rol == null || rol.isEmpty()) {
                abortWithUnauthorized(requestContext, "El token no contiene un rol válido.");
                return;
            }

            // Comprobar la anotación @Secured en el método y luego en la clase
            Method method = resourceInfo.getResourceMethod();
            Class<?> resourceClass = resourceInfo.getResourceClass();

            if (isSecured(method)) {
                List<String> rolesAllowed = getRolesAllowed(method.getAnnotation(Secured.class));
                if (rolesAllowed.isEmpty() || !rolesAllowed.contains(rol)) {
                    abortWithUnauthorized(requestContext, "El usuario no tiene los permisos necesarios.");
                    return;
                }
            } else if (isSecured(resourceClass)) {
                List<String> rolesAllowed = getRolesAllowed(resourceClass.getAnnotation(Secured.class));
                if (rolesAllowed.isEmpty() || !rolesAllowed.contains(rol)) {
                    abortWithUnauthorized(requestContext, "El usuario no tiene los permisos necesarios.");
                    return;
                }
            }

        } catch (ExpiredJwtException e) {
            abortWithUnauthorized(requestContext, "El token ha expirado.");
        } catch (SignatureException e) {
            abortWithUnauthorized(requestContext, "La firma del token es inválida.");
        } catch (MalformedJwtException e) {
            abortWithUnauthorized(requestContext, "El token está mal formado.");
        } catch (UnsupportedJwtException e) {
            abortWithUnauthorized(requestContext, "El token no es soportado.");
        } catch (IllegalArgumentException e) {
            abortWithUnauthorized(requestContext, "Argumento inválido al procesar el token.");
        } catch (IllegalStateException e) {
            LOGGER.log(Level.SEVERE, "Error de configuración del servidor: " + e.getMessage());
            requestContext.abortWith(
                Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Error interno del servidor.\"}")
                    .build());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error inesperado durante la validación del token", e);
            abortWithUnauthorized(requestContext, "Error inesperado al procesar el token.");
        }
    }

    private boolean isSecured(Method method) {
        return method != null && method.isAnnotationPresent(Secured.class);
    }

    private boolean isSecured(Class<?> resourceClass) {
        return resourceClass != null && resourceClass.isAnnotationPresent(Secured.class);
    }

    private List<String> getRolesAllowed(Secured secured) {
        return Arrays.asList(secured.rolesAllowed());
    }

    private void abortWithUnauthorized(ContainerRequestContext requestContext, String message) {
        requestContext.abortWith(
            Response.status(Response.Status.UNAUTHORIZED)
                .header("Content-Type", "application/json")
                .entity(String.format("{\"error\":\"%s\"}", message))
                .build());
    }
}
