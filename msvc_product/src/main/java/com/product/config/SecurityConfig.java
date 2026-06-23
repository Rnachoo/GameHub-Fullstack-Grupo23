package com.product.config;

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
                        // Documentación abierta
                        .requestMatchers("/docs/**", "/swagger-ui/**", "/v3/api-docs/**", "/h2-console/**").permitAll()

                        // ACCESO DE CLIENTES (ADMIN y USER)
                        // Procesar un pago propio (POST)
                        .requestMatchers(HttpMethod.POST, "/api/v1/payments").hasAnyRole("ADMIN", "USER")
                        // Consultar comprobante por ID o por Orden (GET)
                        .requestMatchers(HttpMethod.GET, "/api/v1/payments/{id}").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/payments/orden/**").hasAnyRole("ADMIN", "USER")

                        // OPERACIONES EXCLUSIVAS DE ADMINISTRACIÓN (Solo ADMIN)
                        // Buscar todos los pagos del sistema por estado (auditoría financiera)
                        .requestMatchers(HttpMethod.GET, "/api/v1/payments/estado/**").hasRole("ADMIN")

                        // Modificar estados manualmente o anular pagos (CORREGIDO: Se cambió ** por *)
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/payments/*/estado").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/payments/*/anular").hasRole("ADMIN")

                        .anyRequest().authenticated())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(oauth -> oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(converter)))
                .headers(h -> h.frameOptions(f -> f.disable()));

        return http.build();
    }
}