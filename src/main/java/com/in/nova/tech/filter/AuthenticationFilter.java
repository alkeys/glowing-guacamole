/*
Copyright (c) 2025 Alexander Aviles
Licencia: Creative Commons Attribution-NonCommercial 4.0 International
Prohibido su uso con fines comerciales.
Ver: https://creativecommons.org/licenses/by-nc/4.0/
*/
package com.in.nova.tech.filter;

import com.in.nova.tech.utils.JwtUtil;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;

@Secured
@Provider
public class AuthenticationFilter implements ContainerRequestFilter {

    @Inject
    private JwtUtil jwtUtil;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        // Obtener el encabezado HTTP Authorization de la solicitud
        String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        // Verificar si el encabezado HTTP Authorization está presente y tiene el formato correcto
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            requestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED).entity("Se debe proporcionar el encabezado de autorización").build());
            return;
        }

        // Extraer el token del encabezado HTTP Authorization
        String token = authorizationHeader.substring("Bearer".length()).trim();

        try {
            // Validar el token
            String username = jwtUtil.extractUsername(token);
            // Se puede agregar más validación aquí, como verificar si el usuario existe en la base de datos
            if (!jwtUtil.validateToken(token, username)) {
                requestContext.abortWith(
                        Response.status(Response.Status.UNAUTHORIZED).entity("Token inválido").build());
            }

        } catch (Exception e) {
            requestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED).entity("Token inválido").build());
        }
    }
}
