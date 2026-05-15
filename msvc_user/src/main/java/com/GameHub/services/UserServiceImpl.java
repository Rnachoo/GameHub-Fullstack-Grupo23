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

    @Transactional(readOnly = true)
    @Override
    public List<User> findAll() {
        return this.userRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> findByRol(String rol) {//Filtra por el rol del user
        return this.userRepository.findByRol(rol);
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> findByEstado(String estado){//Filtra por el estado del user
        return this.userRepository.findByEstado(estado);
    }

    @Transactional(readOnly = true)
    @Override
    public User findById(Long id) {
        return this.userRepository.findById(id).orElseThrow(
                ()-> new UserException("User con ID " +id+ " no encontrado")
        );
    }

    @Transactional
    @Override
    public User save(User user) { //Crear cuenta
        if(this.userRepository.existeEmail(user.getEmail())){
            throw new RuntimeException("El correo electronico ya esta registrado");
        }
        user.setEstado("Active");
        return this.userRepository.save(user);
    }

    @Transactional
    @Override
    public User desactiveById(Long id) { //Desactivar cuentas
        User user = findById(id);
        user.setEstado("Inactive");
        return userRepository.save(user);
    }

    @Transactional
    @Override
    public User updateTelefono(Long id, String telefono) {//Updatear el telefono;
        return this.userRepository.findById(id).map(element ->{
            element.setTelefono(telefono);
            return this.userRepository.save(element);
        }).orElseThrow(
                ()  -> new UserException("Cuenta no encontrada, no se puede actualizar el telefono")
        );
    }

    @Transactional
    @Override
    public User updateDirection(Long id, Direction direction) {//Updatear la dirección
        return this.userRepository.findById(id).map(element ->{
            direction.setUser(element);
            element.getDirections().add(direction);
            return this.userRepository.save(element);
        }).orElseThrow(
                ()  -> new UserException("Cuenta no encontrada, no se puede actualizar el telefono")
        );
    }
}
