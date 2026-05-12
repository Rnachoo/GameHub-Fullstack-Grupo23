package com.GameHub.services;

import com.GameHub.models.Auth;

import java.util.List;

public interface AuthService {
List<Auth> findAll();
Auth findById(Long id);
Auth findByEmail(String email);
Auth save(Auth auth);
void deleteById(Long id);
Auth updatePassword(Long id, String newPassword);
Auth updateRol(Long id, String newRol);
Auth updateEstado(Long id, String newEstado);
}
