package com.GameHub.config;

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
                        // Endpoints públicos de documentación y consola
                        .requestMatchers("/docs/**", "/swagger-ui/**", "/v3/api-docs/**", "/h2-console/**").permitAll()

                        // OPERACIONES DE CLIENTE (ADMIN y USER)
                        // Generar compras (POST)
                        .requestMatchers(HttpMethod.POST, "/api/v1/ordenes").hasAnyRole("ADMIN", "USER")
                        // Ver orden propia por ID (GET)
                        .requestMatchers(HttpMethod.GET, "/api/v1/ordenes/{id}").hasAnyRole("ADMIN", "USER")
                        // Ver historial de compras por cliente (GET)
                        .requestMatchers(HttpMethod.GET, "/api/v1/ordenes/cliente/**").hasAnyRole("ADMIN", "USER")
                        // Cancelar una orden pendiente (PATCH)
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/ordenes/**/cancelar").hasAnyRole("ADMIN", "USER")

                        // CONTROL DE LOGÍSTICA INTERNA (Solo ADMIN)
                        // Buscar órdenes globales de todo el sistema filtradas por su estado
                        .requestMatchers(HttpMethod.GET, "/api/v1/ordenes/estado/**").hasRole("ADMIN")
                        // Avanzar estados de la orden como ENVIADO o ENTREGADO (PUT)
                        .requestMatchers(HttpMethod.PUT, "/api/v1/ordenes/**/estado").hasRole("ADMIN")

                        .anyRequest().authenticated())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(oauth -> oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(converter)))
                .headers(h -> h.frameOptions(f -> f.disable()));

        return http.build();
    }
}