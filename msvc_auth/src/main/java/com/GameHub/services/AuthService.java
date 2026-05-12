package com.GameHub.services;

import com.GameHub.models.Auth;

import java.util.List;

public interface AuthService {
List<Auth> findAll();
Auth findById(Long id);
Auth findByEmail(String email);
Auth save(Auth auth);
void desactiveById(Long id);
Auth updatePassword(Long id, Auth auth);
Auth updateRol(Long id, Auth auth);}
