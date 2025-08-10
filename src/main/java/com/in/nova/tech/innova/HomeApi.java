package com.in.nova.tech.innova;


import jakarta.ws.rs.Path;
import java.io.Serializable;

@Path("/home")
public class HomeApi implements Serializable {

    private static final long serialVersionUID = 1L;

    // Aquí puedes agregar métodos para manejar las rutas de la API
    // Por ejemplo, un método para obtener información de inicio

    /**
     * Método que devuelve un mensaje de bienvenida.
     * @return Un mensaje de bienvenida a la API.
     */
    @Path("/")
    public String getInfo() {
        return "Bienvenido a la API de Innovación Nova Tech";
    }
    /**
     * Método que devuelve la versión de la API.
     * @return La versión de la API.
     */
    @Path("/version")
    public String getVersion() {
        return "Versión 1.0 de la API de Innovación Nova Tech";
    }
    /**
     * Método que devuelve información sobre la API. y como usarla. ocupando OpenAPI.
     * Este método puede ser utilizado para proporcionar detalles sobre los endpoints disponibles, parámetros, etc.
     * @return Información sobre la API.
     */
    @Path("/info")
    public String getApiInfo() {
        String apiInfo = "API de Innovación Nova Tech\n" +
                "Esta API permite interactuar con los servicios de Innovación Nova Tech.\n" +
                "Utiliza OpenAPI para documentar los endpoints disponibles.\n" +
                "Para más información, visita nuestra documentación en línea."+
                "\nEndpoints disponibles:\n" +
                " - Documentación OpenAPI UI: http://localhost:9090/openapi/ui/\n" +
                " - Esquema OpenAPI: http://localhost:9090/openapi/\n" +
                " - Health Check: http://localhost:9090/health/\n" +
                " - JWT: http://localhost:9090/jwt/\n" +
                " - Aplicación principal: http://localhost:9090/innova-1.0-SNAPSHOT/"
                ;
        return apiInfo;

    }
}
