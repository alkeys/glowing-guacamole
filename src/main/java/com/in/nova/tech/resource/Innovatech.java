package com.in.nova.tech.resource;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@ApplicationPath("/")
public class Innovatech extends Application {
    // Esta clase sirve como punto de entrada para la aplicación RESTful.
    // No es necesario implementar métodos adicionales aquí,
    // ya que la anotación @ApplicationPath ya configura el contexto base de la aplicación REST.
    // Los recursos REST se definirán en otras clases anotadas con @Path.
}
