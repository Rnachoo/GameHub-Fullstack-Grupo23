package com.inventory.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
public class SecurityConfig {

    @Value("${jwt.secret:EstaEsUnaClaveSecretaSuperSeguraYLargaDeAlMenos32Caracteres}")
    private String secret;

    @Bean
    public JwtDecoder jwtDecoder() {
        SecretKey key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(key).macAlgorithm(MacAlgorithm.HS256).build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter authorities = new JwtGrantedAuthoritiesConverter();
        authorities.setAuthoritiesClaimName("roles");
        authorities.setAuthorityPrefix("");
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authorities);
        return converter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationConverter converter) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Rutas de documentación y consola H2 abiertas
                        .requestMatchers("/docs/**", "/swagger-ui/**", "/v3/api-docs/**", "/h2-console/**").permitAll()

                        // LECTURA DE STOCK (GET): Permitido a ADMIN y USER para verificar disponibilidad de productos
                        .requestMatchers(HttpMethod.GET, "/api/v1/inventories/**").hasAnyRole("ADMIN", "USER")

                        // PROCESOS TRANSACCIONALES (PUT): Reservar, liberar o confirmar stock operativo en compras (ADMIN y USER)
                        .requestMatchers(HttpMethod.PUT, "/api/v1/inventories/producto/**").hasAnyRole("ADMIN", "USER")

                        // MANTENIMIENTO Y CONTROL (POST, PATCH, DELETE): Crear, modificar cantidades manualmente o borrar (Solo ADMIN)
                        .requestMatchers("/api/v1/inventories/**").hasRole("ADMIN")

                        .anyRequest().authenticated())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(oauth -> oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(converter)))
                .headers(h -> h.frameOptions(f -> f.disable()));

        return http.build();
    }
}