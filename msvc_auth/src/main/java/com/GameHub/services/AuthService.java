package com.GameHub.services;

import com.GameHub.models.Auth;
import com.GameHub.models.dtos.AuthDetalleDTO;
import com.GameHub.models.dtos.AuthSaveDTO;
import com.GameHub.models.dtos.AuthUpdatePasswordDTO;
import com.GameHub.models.dtos.AuthUpdateRolDTO;

import java.util.List;

public interface AuthService {
List<AuthDetalleDTO> findAll();
AuthDetalleDTO findById(Long id);
AuthDetalleDTO findByEmail(String email);
AuthDetalleDTO save(AuthSaveDTO authSaveDTO);
AuthDetalleDTO desactiveById(Long id);
AuthDetalleDTO updatePassword(Long id, AuthUpdatePasswordDTO passwordDTO);
AuthDetalleDTO updateRol(Long id, AuthUpdateRolDTO rolDTO);}
