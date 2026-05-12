package com.GameHub.services;

import com.GameHub.exceptions.AuthException;
import com.GameHub.models.Auth;
import com.GameHub.repositories.AuthRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public class AuthServiceImpl implements AuthService {
    private AuthRepository authRepository;


    @Transactional(readOnly = true)
    @Override
    public List<Auth> findAll() {
        return this.authRepository.findAll();
    }

    @Override
    public Auth findById(Long id) {
        return this.authRepository.findById(id).orElseThrow(
                () -> new AuthException("Cuenta con ID " +id+" no encontrado")
        );
    }

    @Transactional(readOnly = true)
    @Override
    public Auth findByEmail(String email) {
        return this.authRepository.findByEmail(email).orElseThrow(
                () -> new AuthException("Cuenta con Email "+email+" no encontrado")
        ) ;
    }

    @Override
    public Auth save(Auth auth) {
        if(this.authRepository.findByEmail(auth.getEmail()).isPresent()){
            throw new AuthException("Cuenta con Email "+auth.getEmail()+" ya existe");
        }
        return this.authRepository.save(auth);
    }

    @Override
    public void desactiveById(Long id){
        Auth auth = findById(id);
        auth.setEstado("Inactivo");
        authRepository.save(auth);
    }

    @Transactional
    @Override
    public Auth updatePassword(Long id, Auth auth) {
        return this.authRepository.findById(id).map(element ->{
            element.setPasswordHash(auth.getPasswordHash());
            return this.authRepository.save(element);
        }).orElseThrow(
                () -> new AuthException("Cuenta no encontrada")
        );
    }

    @Override
    public Auth updateRol(Long id, Auth auth) {
        return this.authRepository.findById(id).map(element ->{
            element.setRol(auth.getRol());
            return this.authRepository.save(element);
        }).orElseThrow(
                () -> new AuthException("Cuenta no encontrada")
        );
    }
}
