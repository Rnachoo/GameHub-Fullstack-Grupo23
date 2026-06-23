package com.auth.security;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

// Configuracion de seguridad del servicio de autenticación.
// Aqui se firma el token (JwtEncoder) al hacer login y tambien se valida (JwtDecoder),
// porque este servicio tiene endpoints protegidos (ej. desactivar cuenta o cambiar rol = ADMIN).
@Configuration
@EnableMethodSecurity   // habilita @PreAuthorize en los controllers
public class SecurityConfig {

    // Clave secreta COMPARTIDA por todos los servicios. En produccion iria en una variable de entorno.
    // Usamos un valor por defecto en caso de que no esté definida en el application.properties
    @Value("${jwt.secret:EstaEsUnaClaveSecretaSuperSeguraYLargaDeAlMenos32Caracteres}")
    private String secret;

    private SecretKey claveHmac() {
        // HS256 necesita una clave de al menos 256 bits (32 bytes).
        return new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }

    // FIRMA tokens con la clave secreta.
    @Bean
    public JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(claveHmac()));
    }

    // VALIDA tokens con la misma clave secreta.
    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withSecretKey(claveHmac()).macAlgorithm(MacAlgorithm.HS256).build();
    }

    // Cifra las contrasenas (nunca se guardan en texto plano).
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Convierte el claim "roles" del token en authorities de Spring (sin agregar prefijo,
    // porque ya guardamos los roles como ROLE_X en la base de datos).
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
                .csrf(csrf -> csrf.disable())                       // API stateless: no usamos CSRF
                .authorizeHttpRequests(auth -> auth
                        // login y registro son publicos (aun no hay token)
                        // Asegúrate de que esta ruta coincida con el RequestMapping de tu AuthController
                        .requestMatchers("/auths/login", "/auths/save").permitAll()
                        // documentacion y consola h2 abiertas para la demo
                        .requestMatchers("/docs/**", "/swagger-ui/**", "/v3/api-docs/**", "/h2-console/**").permitAll()
                        // el resto exige token valido
                        .anyRequest().authenticated())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // este servicio tambien es Resource Server: valida el JWT en sus endpoints protegidos
                .oauth2ResourceServer(oauth -> oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(converter)))
                .headers(h -> h.frameOptions(f -> f.disable())); // para que se vea la consola h2
        return http.build();
    }
}