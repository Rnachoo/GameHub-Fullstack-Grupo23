package com.GameHub.security;

import com.GameHub.models.Auth;
import com.GameHub.models.Rol;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

// Genera (FIRMA) el JWT que el resto del sistema validara.
@Service
public class JwtService {

    private final JwtEncoder jwtEncoder;

    // Minutos de validez del token (configurable). Por defecto 60 min.
    @Value("${jwt.expiration-minutes:60}")
    private long expirationMinutes;

    public JwtService(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    public String generarToken(Auth auth) {
        Instant ahora = Instant.now();

        List<String> roles = auth.getRoles().stream().map(Rol::getNombre).toList();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("msvc-auth")                           // quien emitio el token
                .issuedAt(ahora)                               // cuando
                .expiresAt(ahora.plus(expirationMinutes, ChronoUnit.MINUTES)) // hasta cuando vale
                .subject(auth.getEmail())                      // a quien pertenece (usamos email como identificador entre msvc's)
                .claim("nombreCuenta", auth.getNombreCuenta()) // agregamos el nombre de cuenta como extra
                .claim("roles", roles)                         // permisos
                .build();

        // Cabecera: algoritmo HMAC-SHA256 (clave secreta compartida).
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }
}