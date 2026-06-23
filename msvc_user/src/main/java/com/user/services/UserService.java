package com.user.services;

import com.user.models.dtos.UserDetalleDTO;
import com.user.models.dtos.UserSaveDTO;
import com.user.models.dtos.UserUpdateDirectionDTO;
import com.user.models.dtos.UserUpdateTelefonoDTO;

import java.util.List;

public interface UserService {

    List<UserDetalleDTO> findAll();
    UserDetalleDTO findByEmail(String email);
    List<UserDetalleDTO> findByEstado(String estado);
    UserDetalleDTO findById (Long id);
    UserDetalleDTO save (UserSaveDTO userSaveDTO);
    UserDetalleDTO desactiveById(Long id);
    UserDetalleDTO updateTelefono(Long id, UserUpdateTelefonoDTO telefonoDTO);
    UserDetalleDTO updateDirection(Long id, UserUpdateDirectionDTO directionDTO);
}
