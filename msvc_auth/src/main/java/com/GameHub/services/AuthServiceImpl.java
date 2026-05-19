package com.GameHub.services;

import com.GameHub.clients.UserClient;
import com.GameHub.exceptions.AuthException;
import com.GameHub.models.Auth;
import com.GameHub.models.dtos.AuthDetalleDTO;
import com.GameHub.models.dtos.UserDTO;
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
        log.info("Abriendo Listando cuentas registradas en el sistema!");
        return this.authRepository.findAll().stream().map(auth -> {
            AuthDetalleDTO dto = new AuthDetalleDTO();
            dto.setId(auth.getId());
            dto.setEstado(auth.getEstado());
            dto.setEmail(auth.getEmail());
            dto.setRol(auth.getRol());
            try {
                UserDTO user = this.userClient.getUserById(auth.getId());
                dto.setUser(user);
            }catch (FeignException e){
                log.error("Error de Conexión con el id "+ auth.getId());
                throw new RuntimeException("Cuenta con ID "+ auth.getId()+" no existe");
            }
            return dto;
        }).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public AuthDetalleDTO findById(Long id) { //Buscar Por el ID
        log.info("Buscando cuentas registradas en el sistema!");
        Auth auth = this.authRepository.findById(id).orElseThrow(
                () -> new AuthException("Cuenta con ID " + id + " no encontrado"));
        AuthDetalleDTO dto = new AuthDetalleDTO();
        dto.setId(auth.getId());
        dto.setEmail(auth.getEmail());
        dto.setEstado(auth.getEstado());
        dto.setRol(auth.getRol());
        try {
            UserDTO user = this.userClient.getUserById(auth.getId());
            dto.setUser(user);
        } catch (FeignException e) {
            log.error("Error de Conexión con el id "+ auth.getId());
            throw new RuntimeException("Cuenta con ID "+ auth.getId()+" no existe");
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
            UserDTO user = this.userClient.getUserById(auth.getId());
            dto.setUser(user);
        } catch (FeignException e) {
            log.error("Error de Conexión con el id "+ auth.getId());
            throw new RuntimeException("Cuenta con email "+ auth.getEmail()+" no existe");
        }
        return dto;
    }

    @Transactional
    @Override
    public Auth save(Auth auth) { //Crear cuenta
        if(this.authRepository.findByEmail(auth.getEmail()).isPresent()){
            throw new AuthException("Cuenta con Email "+auth.getEmail()+" ya esta registrado");
        }
        auth.setEstado("Active");
        log.info("Cuenta con email" + auth.getEmail()+" Creada con exito!");
        return this.authRepository.save(auth);
    }

    @Transactional
    @Override
    public Auth desactiveById(Long id){//Desactivar cuentas
        Auth auth = this.authRepository.findById(id).orElseThrow(
                () -> new AuthException("Cuenta con ID " + id + " no encontrado"));
        auth.setEstado("Inactivo");//Funciona por estado Active o Inactive, la idea es imposibilitar su uso sin borrar los datos
        log.info("Cuenta con id "+id+" Ha sido desactivada");
        return authRepository.save(auth);
    }


    @Transactional
    @Override
    public Auth updatePassword(Long id, String passwordHash) {//Updatear la clave de acceso (Revisar antes de entrega)
        return this.authRepository.findById(id).map(element ->{
            element.setPasswordHash(passwordHash);
            log.info("Contraseña actuliazada con exito!");
            return this.authRepository.save(element);

        }).orElseThrow(
                () -> new AuthException("Cuenta no encontrada")
        );
    }

    @Transactional
    @Override
    public Auth updateRol(Long id, String rol) {//Updatear el rol de la cuenta
        return this.authRepository.findById(id).map(element ->{
            element.setRol(rol);
            log.info("Rol actualizado con exito");
            return this.authRepository.save(element);
        }).orElseThrow(
                () -> new AuthException("Cuenta no encontrada, no se puede actualizar el rol")
        );
    }


    //NOTA IMPORTANTE: FALTA APLICAR BCrypt!!!
}
