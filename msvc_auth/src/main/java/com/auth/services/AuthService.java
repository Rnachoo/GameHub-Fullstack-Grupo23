package com.auth.services;

import com.auth.models.dtos.AuthDetalleDTO;
import com.auth.models.dtos.AuthLoginDTO;
import com.auth.models.dtos.AuthSaveDTO;
import com.auth.models.dtos.AuthUpdatePasswordDTO;
import com.auth.models.dtos.AuthUpdateRolDTO;

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