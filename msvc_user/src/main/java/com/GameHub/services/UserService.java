package com.GameHub.services;

import com.GameHub.models.Direction;
import com.GameHub.models.User;
import com.GameHub.models.dtos.UserDetalleDTO;
import com.GameHub.models.dtos.UserSaveDTO;
import com.GameHub.models.dtos.UserUpdateDirectionDTO;
import com.GameHub.models.dtos.UserUpdateTelefonoDTO;

import java.util.List;

public interface UserService {

    List<UserDetalleDTO> findAll();
    List<UserDetalleDTO> findByRol(String rol);
    List<UserDetalleDTO> findByEmail(String email);
    List<UserDetalleDTO> findByEstado(String estado);
    UserDetalleDTO findById (Long id);
    UserDetalleDTO save (UserSaveDTO userSaveDTO);
    UserDetalleDTO desactiveById(Long id);
    UserDetalleDTO updateTelefono(Long id, UserUpdateTelefonoDTO telefonoDTO);
    UserDetalleDTO updateDirection(Long id, UserUpdateDirectionDTO directionDTO);
}
