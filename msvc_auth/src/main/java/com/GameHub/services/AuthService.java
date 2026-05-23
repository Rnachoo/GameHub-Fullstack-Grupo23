package com.GameHub.services;

import com.GameHub.models.dtos.*;

import java.util.List;

public interface AuthService {
List<AuthDetalleDTO> findAll();
AuthDetalleDTO findById(Long id);
AuthDetalleDTO findByEmail(String email);
AuthDetalleDTO save(AuthSaveDTO authSaveDTO);
AuthDetalleDTO desactiveById(Long id);
AuthDetalleDTO updatePassword(Long id, AuthUpdatePasswordDTO passwordDTO);
AuthDetalleDTO updateRol(Long id, AuthUpdateRolDTO rolDTO);
AuthDetalleDTO login(AuthLoginDTO authLoginDTO);}