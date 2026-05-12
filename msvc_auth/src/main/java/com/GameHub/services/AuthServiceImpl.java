package com.GameHub.services;

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
        return null;
    }

    @Override
    public Auth findByEmail(String email) {
        return null;
    }

    @Override
    public Auth save(Auth auth) {
        return null;
    }

    @Override
    public void deleteById(Long id) {

    }

    @Override
    public Auth updatePassword(Long id, String newPassword) {
        return null;
    }

    @Override
    public Auth updateRol(Long id, String newRol) {
        return null;
    }

    @Override
    public Auth updateEstado(Long id, String newEstado) {
        return null;
    }
}
