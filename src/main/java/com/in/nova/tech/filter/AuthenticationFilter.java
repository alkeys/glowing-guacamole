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
            // Validar token y extraer claims
            Claims claims = jwtUtil.validateToken(token);

            // Token válido → continuar con la solicitud
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
            // Error en la configuración (secret nulo)
            LOGGER.log(Level.SEVERE, "Error de configuración del servidor: " + e.getMessage());
            abortWithUnauthorized(requestContext, "Error interno de autenticación.");
        } catch (Exception e) {
            // Error inesperado, loguear pero no abortar con 500
            LOGGER.log(Level.SEVERE, "Error inesperado durante la validación del token", e);
            abortWithUnauthorized(requestContext, "Error inesperado al procesar el token.");
        }
    }

    private void abortWithUnauthorized(ContainerRequestContext requestContext, String message) {
        requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED)
                        .header("Content-Type", "application/json")
                        .entity(String.format("{\"error\":\"%s\"}", message))
                        .build()
        );
    }
}
