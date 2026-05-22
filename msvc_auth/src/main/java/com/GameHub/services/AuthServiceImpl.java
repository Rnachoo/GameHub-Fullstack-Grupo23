package com.GameHub.services;

import com.GameHub.clients.UserClient;
import com.GameHub.exceptions.AuthException;
import com.GameHub.models.Auth;
import com.GameHub.models.dtos.*;
import com.GameHub.repositories.AuthRepository;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {
    @Autowired
    private AuthRepository authRepository;
    @Autowired
    private UserClient userClient;

    @Transactional(readOnly = true)
    @Override
    public List<AuthDetalleDTO> findAll() {//Listar todas las cuentas
        log.info("Listando cuentas registradas en el sistema!");
        return this.authRepository.findAll().stream().map(auth -> {
            AuthDetalleDTO dto = new AuthDetalleDTO();
            dto.setId(auth.getId());
            dto.setEstado(auth.getEstado());
            dto.setEmail(auth.getEmail());
            dto.setRol(auth.getRol());
            try {
                UserDTO user = this.userClient.getUserByEmail(auth.getEmail());
                dto.setUser(user);
            }catch (FeignException e){
                log.error("Error de Conexión con el email "+ auth.getEmail());
                throw new RuntimeException("Cuenta con email "+ auth.getEmail()+" no existe");
            }
            return dto;
        }).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public AuthDetalleDTO findById(Long id){
        log.info("Buscando cuentas registradas en el sistema!");
        Auth auth = this.authRepository.findById(id).orElseThrow(
                ()-> new AuthException("Cuenta con ID " +id+" no encontrada"));
        AuthDetalleDTO dto = new AuthDetalleDTO();
        dto.setId(auth.getId());
        dto.setEmail(auth.getEmail());
        dto.setEstado(auth.getEstado());
        dto.setRol(auth.getRol());
        try {
            UserDTO user = this.userClient.getUserByEmail(auth.getEmail());
            dto.setUser(user);
        }catch (FeignException e){
            log.error("Error de Conexion con el usuario con correo "+ auth.getEmail());
            throw new RuntimeException("Cuenta con email "+ auth.getEmail()+" no existe");
        }
        return dto;
    }


    @Transactional(readOnly = true)
    @Override
    public AuthDetalleDTO findByEmail(String email) {//Buscar cuenta por email
        log.info("Buscando cuentas registradas en el sistema!");
        Auth auth = this.authRepository.findByEmail(email).orElseThrow(
                () -> new AuthException("Cuenta con Email " + email + " no encontrado"));
        AuthDetalleDTO dto = new AuthDetalleDTO();
        dto.setId(auth.getId());
        dto.setEmail(auth.getEmail());
        dto.setEstado(auth.getEstado());
        dto.setRol(auth.getRol());
        try {
            UserDTO user = this.userClient.getUserByEmail(auth.getEmail());
            dto.setUser(user);
        } catch (FeignException e) {
            log.error("Error de conexión con user con email "+ auth.getEmail());
            throw new RuntimeException("Cuenta con email "+ auth.getEmail()+" encontrada, pero no se puede obtener la información.");
        }
        return dto;
    }

    @Transactional
    @Override
    public AuthDetalleDTO save(AuthSaveDTO authSaveDTO) { //Crear cuenta
        if(this.authRepository.findByEmail(authSaveDTO.getEmail()).isPresent()){
            throw new AuthException("Cuenta con Email "+authSaveDTO.getEmail()+" ya esta registrado");
        }
        Auth auth = new Auth();
        auth.setEmail(authSaveDTO.getEmail());
        auth.setRol(authSaveDTO.getRol());
        auth.setPassword(authSaveDTO.getPassword());
        auth.setEstado("Active");

        auth = authRepository.save(auth);
        log.info("Cuenta con email " + auth.getEmail()+" Creada con exito!");
        AuthDetalleDTO dto = new AuthDetalleDTO();
        dto.setId(auth.getId());
        dto.setEmail(auth.getEmail());
        dto.setRol(auth.getRol());
        dto.setEstado(auth.getEstado());

        return dto;
    }

    @Transactional
    @Override
    public AuthDetalleDTO desactiveById(Long id){//Desactivar cuentas
        Auth auth = this.authRepository.findById(id).orElseThrow(
                () -> new AuthException("Cuenta con ID " + id + " no encontrado"));

        auth.setEstado("Inactive");//Funciona por estado Active o Inactive, la idea es imposibilitar su uso sin borrar los datos
        auth = authRepository.save(auth);
        log.info("Cuenta con id "+id+" Ha sido desactivada");

        AuthDetalleDTO dto = new AuthDetalleDTO();
        dto.setId(auth.getId());
        dto.setEmail(auth.getEmail());
        dto.setRol(auth.getRol());
        dto.setEstado(auth.getEstado());

        return dto;
    }


    @Transactional
    @Override
    public AuthDetalleDTO updatePassword(Long id, AuthUpdatePasswordDTO passwordDTO) {//Updatear la clave de acceso (Revisar antes de entrega)
        return this.authRepository.findById(id).map(auth ->{
            auth.setPassword(passwordDTO.getPassword());
            log.info("Contraseña actuliazada con exito!");

            auth = this.authRepository.save(auth);

            AuthDetalleDTO dto = new AuthDetalleDTO();
            dto.setId(auth.getId());
            dto.setEmail(auth.getEmail());
            dto.setRol(auth.getRol());
            dto.setEstado(auth.getEstado());
            return dto;

        }).orElseThrow(
                () -> new AuthException("Cuenta no encontrada")
        );
    }

    @Transactional
    @Override
    public AuthDetalleDTO updateRol(Long id, AuthUpdateRolDTO rolDTO) {//Updatear el rol de la cuenta
        return this.authRepository.findById(id).map(auth ->{
            auth.setRol(rolDTO.getRol());
            log.info("Rol actualizado con exito");
            auth = this.authRepository.save(auth);

            AuthDetalleDTO dto = new AuthDetalleDTO();
            dto.setId(auth.getId());
            dto.setEmail(auth.getEmail());
            dto.setRol(auth.getRol());
            dto.setEstado(auth.getEstado());
            return dto;

        }).orElseThrow(
                () -> new AuthException("Cuenta no encontrada, no se puede actualizar el rol")
        );
    }
}
