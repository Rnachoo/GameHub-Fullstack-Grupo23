package com.GameHub.services;

import com.GameHub.models.Direction;
import com.GameHub.models.User;
import com.GameHub.models.dtos.UserDTO;

import java.util.List;

public interface UserService {

    List<UserDTO> findAll();
    List<UserDTO> findByRol(String rol);
    List<UserDTO> findByEstado(String estado);
    UserDTO findById (Long id);
    User save (User user);
    User desactiveById(Long id);
    User updateTelefono(Long id, String telefono);
    User updateDirection(Long id, Direction direction);
}
