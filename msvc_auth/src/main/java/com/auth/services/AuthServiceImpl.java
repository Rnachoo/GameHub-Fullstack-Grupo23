package com.auth.services;

import com.auth.clients.UserClient;
import com.auth.exceptions.AuthException;
import com.auth.models.Auth;
import com.auth.models.Rol;
import com.auth.models.dtos.*;
import com.auth.models.dtos.AuthDetalleDTO;
import com.auth.models.dtos.AuthLoginDTO;
import com.auth.models.dtos.AuthSaveDTO;
import com.auth.models.dtos.AuthUpdatePasswordDTO;
import com.auth.models.dtos.AuthUpdateRolDTO;
import com.auth.models.dtos.UserDTO;
import com.auth.repositories.AuthRepository;
import com.auth.repositories.RolRepository;
import com.auth.security.JwtService;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private UserClient userClient;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Transactional(readOnly = true)
    @Override
    public List<AuthDetalleDTO> findAll() {
        log.info("Listando cuentas registradas en el sistema!");
        return this.authRepository.findAll().stream().map(auth -> {
            AuthDetalleDTO dto = new AuthDetalleDTO();
            dto.setId(auth.getId());
            dto.setEstado(auth.getEstado());
            dto.setEmail(auth.getEmail());
            dto.setRol(auth.getRoles().isEmpty() ? "SIN_ROL" : auth.getRoles().iterator().next().getNombre());

            try {
                UserDTO user = this.userClient.getUserByEmail(auth.getEmail());
                dto.setUser(user);
            } catch (FeignException e){
                log.error("Error de Conexión con el email " + auth.getEmail());
            }
            return dto;
        }).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public AuthDetalleDTO findById(Long id){
        log.info("Buscando cuentas registradas en el sistema por ID!");
        Auth auth = this.authRepository.findById(id).orElseThrow(
                ()-> new AuthException("Cuenta con ID " + id + " no encontrada"));

        AuthDetalleDTO dto = new AuthDetalleDTO();
        dto.setId(auth.getId());
        dto.setEmail(auth.getEmail());
        dto.setEstado(auth.getEstado());
        dto.setRol(auth.getRoles().isEmpty() ? "SIN_ROL" : auth.getRoles().iterator().next().getNombre());

        try {
            UserDTO user = this.userClient.getUserByEmail(auth.getEmail());
            dto.setUser(user);
        } catch (FeignException e){
            log.warn("Error de Conexion con el usuario con correo " + auth.getEmail());
        }
        return dto;
    }

    @Transactional(readOnly = true)
    @Override
    public AuthDetalleDTO findByEmail(String email) {
        log.info("Buscando cuentas registradas en el sistema por Email!");
        Auth auth = this.authRepository.findByEmail(email).orElseThrow(
                () -> new AuthException("Cuenta con Email " + email + " no encontrado"));

        AuthDetalleDTO dto = new AuthDetalleDTO();
        dto.setId(auth.getId());
        dto.setEmail(auth.getEmail());
        dto.setEstado(auth.getEstado());
        dto.setRol(auth.getRoles().isEmpty() ? "SIN_ROL" : auth.getRoles().iterator().next().getNombre());

        try {
            UserDTO user = this.userClient.getUserByEmail(auth.getEmail());
            dto.setUser(user);
        } catch (FeignException e) {
            log.warn("Error de conexión con user con email " + auth.getEmail());
            throw new AuthException("Cuenta con email " + auth.getEmail() + " encontrada, pero no se puede obtener la información.");
        }
        return dto;
    }

    @Transactional
    @Override
    public AuthDetalleDTO save(AuthSaveDTO authSaveDTO) {
        if(this.authRepository.findByEmail(authSaveDTO.getEmail()).isPresent()){
            throw new AuthException("Cuenta con Email " + authSaveDTO.getEmail() + " ya esta registrado");
        }

        Rol rolAsignado = this.rolRepository.findByNombre(authSaveDTO.getRol())
                .orElseThrow(() -> new AuthException("El rol '" + authSaveDTO.getRol() + "' no existe."));

        Auth auth = new Auth();
        auth.setEmail(authSaveDTO.getEmail());
        auth.getRoles().add(rolAsignado);
        auth.setPassword(passwordEncoder.encode(authSaveDTO.getPassword()));
        auth.setEstado("Active");
        auth.setNombreCuenta(authSaveDTO.getNombreCuenta());
        auth.setFechaCreacion(LocalDateTime.now());

        auth = authRepository.save(auth);
        log.info("Cuenta con email " + auth.getEmail() + " Creada con exito!");

        AuthDetalleDTO dto = new AuthDetalleDTO();
        dto.setId(auth.getId());
        dto.setEmail(auth.getEmail());
        dto.setRol(auth.getRoles().iterator().next().getNombre());
        dto.setEstado(auth.getEstado());

        return dto;
    }

    @Transactional
    @Override
    public AuthDetalleDTO desactiveById(Long id){
        Auth auth = this.authRepository.findById(id).orElseThrow(
                () -> new AuthException("Cuenta con ID " + id + " no encontrado"));

        auth.setEstado("Inactive");
        auth = authRepository.save(auth);
        log.info("Cuenta con id " + id + " Ha sido desactivada");

        AuthDetalleDTO dto = new AuthDetalleDTO();
        dto.setId(auth.getId());
        dto.setEmail(auth.getEmail());
        dto.setRol(auth.getRoles().isEmpty() ? "SIN_ROL" : auth.getRoles().iterator().next().getNombre());
        dto.setEstado(auth.getEstado());

        return dto;
    }

    @Transactional
    @Override
    public AuthDetalleDTO updatePassword(Long id, AuthUpdatePasswordDTO passwordDTO) {
        return this.authRepository.findById(id).map(auth ->{
            auth.setPassword(passwordEncoder.encode(passwordDTO.getPassword()));
            log.info("Contraseña actuliazada con exito!");

            auth = this.authRepository.save(auth);

            AuthDetalleDTO dto = new AuthDetalleDTO();
            dto.setId(auth.getId());
            dto.setEmail(auth.getEmail());
            dto.setRol(auth.getRoles().isEmpty() ? "SIN_ROL" : auth.getRoles().iterator().next().getNombre());
            dto.setEstado(auth.getEstado());
            return dto;

        }).orElseThrow(
                () -> new AuthException("Cuenta no encontrada")
        );
    }

    @Transactional
    @Override
    public AuthDetalleDTO updateRol(Long id, AuthUpdateRolDTO rolDTO) {
        return this.authRepository.findById(id).map(auth ->{
            Rol nuevoRol = this.rolRepository.findByNombre(rolDTO.getRol())
                    .orElseThrow(() -> new AuthException("El rol '" + rolDTO.getRol() + "' no existe."));
            auth.getRoles().clear();
            auth.getRoles().add(nuevoRol);

            log.info("Rol actualizado con exito");
            auth = this.authRepository.save(auth);

            AuthDetalleDTO dto = new AuthDetalleDTO();
            dto.setId(auth.getId());
            dto.setEmail(auth.getEmail());
            dto.setRol(auth.getRoles().iterator().next().getNombre());
            dto.setEstado(auth.getEstado());
            return dto;

        }).orElseThrow(
                () -> new AuthException("Cuenta no encontrada, no se puede actualizar el rol")
        );
    }

    @Transactional
    @Override
    public AuthDetalleDTO login(AuthLoginDTO authLoginDTO){
        if(authLoginDTO.getEmail() == null || authLoginDTO.getEmail().isBlank()) {
            throw new AuthException("El email no puede estar vacio");
        }
        if(authLoginDTO.getPassword() == null || authLoginDTO.getPassword().isBlank()){
            throw new AuthException("La contraseña no puede estar vacia");
        }
        Auth auth = this.authRepository.findByEmail(authLoginDTO.getEmail()).orElseThrow(
                () -> new AuthException("Credenciales Invalidas"));

        if(!"Active".equalsIgnoreCase(auth.getEstado())){
            log.warn("La cuenta con email " + auth.getEmail() + " esta desactivada");
            throw new AuthException("El usuario esta inactivo, no se puede iniciar sesión");
        }

        if(!passwordEncoder.matches(authLoginDTO.getPassword(), auth.getPassword())){
            log.warn("Cuenta con email " + auth.getEmail() + " Fallo al escribir la contraseña");
            throw new AuthException("Contraseña invalida");
        }
        log.info("Login exitoso para la cuenta con email " + auth.getEmail());

        String token = jwtService.generarToken(auth);
        log.info("Token generado exitosamente");
        AuthDetalleDTO dto = new AuthDetalleDTO();
        dto.setId(auth.getId());
        dto.setEmail(auth.getEmail());
        dto.setEstado(auth.getEstado());
        dto.setRol(auth.getRoles().isEmpty() ? "SIN_ROL" : auth.getRoles().iterator().next().getNombre());

        return dto;
    }
}