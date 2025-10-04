/*
Copyright (c) 2025 Alexander Aviles
Licencia: Creative Commons Attribution-NonCommercial 4.0 International
Prohibido su uso con fines comerciales.
Ver: https://creativecommons.org/licenses/by-nc/4.0/
*/
package com.in.nova.tech.utils;

import com.in.nova.tech.entity.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Date;
import java.util.function.Function;

@ApplicationScoped
public class JwtUtil {

    // Se ha reemplazado la clave secreta por una clave segura de más de 256 bits.
    // IMPORTANTE: Para producción, esta clave debe ser manejada de forma segura y no estar "hardcodeada".  se debe usar variables de entorno o 
    //un gestor de secretos.
    // Deberías externalizarla a un archivo de configuración (microprofile-config.properties) o a variables de entorno.
    // Clave secreta generada aleatoriamente (256 bits, Base64)
    //private String secret = "k8Jw9vZ1Q2x7Tn5pR3s6Yz8uL4a1Xc0bV5e2Hj9mN7q3Wf6Kp2Dg8Sx4Tz1Lr7Q";
    //ejemplo de variable de entorno (asegúrate de configurarla en tu entorno de ejecución)
    private String secret = System.getenv("JWT_SECRET_KEY");

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(Usuario usuario) {
        Claims claims = Jwts.claims().setSubject(usuario.getNombreUsuario());
        claims.put("role", usuario.getRol()); // Añadimos el rol aquí
        return createToken(claims);
    }

    private String createToken(Claims claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 horas
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }
}
