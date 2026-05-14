package com.GameHub.services;

import com.GameHub.exceptions.AuthException;
import com.GameHub.models.Auth;
import com.GameHub.repositories.AuthRepository;
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

    @Transactional(readOnly = true)
    @Override
    public List<Auth> findAll() {//Listar todas las cuentas
        return this.authRepository.findAll();
    }

    @Transactional
    @Override
    public Auth findById(Long id) {//Buscar cuenta por ID
        return this.authRepository.findById(id).orElseThrow(
                () -> new AuthException("Cuenta con ID " +id+" no encontrado")
        );
    }

    @Transactional(readOnly = true)
    @Override
    public Auth findByEmail(String email) {//Buscar cuenta por email
        return this.authRepository.findByEmail(email).orElseThrow(
                () -> new AuthException("Cuenta con Email "+email+" no encontrado")
        ) ;
    }

    @Transactional
    @Override
    public Auth save(Auth auth) { //Crear cuenta
        if(this.authRepository.findByEmail(auth.getEmail()).isPresent()){
            log.error("Error al crear la cuenta");
            throw new AuthException("Cuenta con Email "+auth.getEmail()+" ya esta registrado");
        }
        auth.setEstado("Active");
        log.info("Cuenta con email" + auth.getEmail()+" Creada con exito!");
        return this.authRepository.save(auth);
    }

    @Override
    public Auth desactiveById(Long id){//Desactivar cuentas
        Auth auth = findById(id);
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
                () -> new AuthException("Cuenta no encontrada")
        );
    }


    //NOTA IMPORTANTE: FALTA APLICAR BCrypt!!!
}
