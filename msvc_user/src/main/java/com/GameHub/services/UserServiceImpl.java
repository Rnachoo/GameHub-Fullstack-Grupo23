package com.GameHub.services;

import com.GameHub.exceptions.UserException;
import com.GameHub.models.Direction;
import com.GameHub.models.User;
import com.GameHub.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.AuthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<User> findAll() {
        return this.userRepository.findAll();
    }

    @Override
    public List<User> findByRol(String rol) {//Filtra por el rol del user
        return this.userRepository.findByRol(rol);
    }

    @Override
    public List<User> findByEstado(String estado){//Filtra por el estado del user
        return this.userRepository.findByEstado(estado);
    }

    @Override
    public User findByID(Long id) {
        return this.userRepository.findById(id).orElseThrow(
                ()-> new UserException("User con ID " +id+ " no encontrado")
        );
    }

    @Override
    public User save(User user) {
    }

    @Override
    public User desactiveById(Long id) {
        return null;
    }

    @Override
    public User updateTelefono(Long id, String telefono) {
        return null;
    }

    @Override
    public User updateDirection(Long id, Direction direction) {
        return null;
    }
}
