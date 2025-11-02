package com.gym.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    // @Value("${jwt.expiration}")
    // private long expirationMs;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    // Token de verificación por email (expira rápido, ej: 24h)
    public String generarTokenVerificacion(String username) {
        long expirationMs = 24 * 60 * 60 * 1000L; // 24 horas
        return Jwts.builder()
                .setSubject(username)
                .claim("tipo", "verificacion")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Token de sesión para endpoints protegidos
    public String generarTokenSesion(String username) {
        long expirationMs = 7 * 24 * 60 * 60 * 1000L; // 7 días
        return Jwts.builder()
                .setSubject(username)
                .claim("tipo", "sesion")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generarTokenRecuperacion(String email) {
        long expirationMs = 15 * 60 * 1000; // 15 minutos
        return Jwts.builder()
                .setSubject(email)
                .claim("tipo", "recuperacion")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extraerTipo(String token) {
        return (String) extraerClaims(token).get("tipo");
    }

    public Claims extraerClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /*
     * public String generarToken(String username) {
     * return Jwts.builder()
     * .setSubject(username)
     * .setIssuedAt(new Date())
     * .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
     * .signWith(getSigningKey(), SignatureAlgorithm.HS256)
     * .compact();
     * }
     */

    public String extraerUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validarToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}
