package com.GameHub.services;

import com.GameHub.models.Auth;
import com.GameHub.models.dtos.AuthDetalleDTO;

import java.util.List;

public interface AuthService {
List<AuthDetalleDTO> findAll();
AuthDetalleDTO findById(Long id);
AuthDetalleDTO findByEmail(String email);
Auth save(Auth auth);
Auth desactiveById(Long id);
Auth updatePassword(Long id, String passwordHash);
Auth updateRol(Long id, String rol);}
