package com.GameHub.services;

import com.GameHub.models.Direction;
import com.GameHub.models.User;

import java.util.List;

public interface UserService {

    List<User> findAll();
    List<User> findByRol(String rol);
    List<User> findByEstado(String estado);
    User findById (Long id);
    User save (User user);
    User desactiveById(Long id);
    User updateTelefono(Long id, String telefono);
    User updateDirection(Long id, Direction direction);
}
