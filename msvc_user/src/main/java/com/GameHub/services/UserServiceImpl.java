package com.GameHub.services;

import com.GameHub.exceptions.UserException;
import com.GameHub.models.Direction;
import com.GameHub.models.User;
import com.GameHub.models.dtos.UserDTO;
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
    public List<UserDTO> findAll() {
        log.info("Listando usuarios registrados en el sistema!");
        return this.userRepository.findAll().stream().map(user -> {
            UserDTO dto = new UserDTO();
            dto.setId(user.getId());
            dto.setNombreUser(user.getNombreUser());
            dto.setEmail(user.getEmail());
            dto.setTelefono(user.getTelefono());
            dto.setRol(user.getRol());
            dto.setEstado(user.getEstado());
            return dto;
        }).toList();
    };

    @Transactional(readOnly = true)
    @Override
    public List<UserDTO> findByRol(String rol) {//Filtra por el rol del user
        log.info("Listando usuarios registrados en el sistema!");
        return this.userRepository.findByRol(rol).stream().map(user -> {
            UserDTO dto = new UserDTO();
            dto.setId(user.getId());
            dto.setNombreUser(user.getNombreUser());
            dto.setEmail(user.getEmail());
            dto.setTelefono(user.getTelefono());
            dto.setRol(user.getRol());
            dto.setEstado(user.getEstado());
            return dto;
        }).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDTO> findByEstado(String estado){//Filtra por el estado del user
        log.info("Listando usuarios registrados en el sistema!");
        return this.userRepository.findByEstado(estado).stream().map(user -> {
            UserDTO dto = new UserDTO();
            dto.setId(user.getId());
            dto.setNombreUser(user.getNombreUser());
            dto.setEmail(user.getEmail());
            dto.setTelefono(user.getTelefono());
            dto.setRol(user.getRol());
            dto.setEstado(user.getEstado());
            return dto;
        }).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public UserDTO findById(Long id) {
        log.info("Buscando usuarios registrados en el sistema!");
        User user = this.userRepository.findById(id).orElseThrow(
                ()-> new UserException("User con ID " + id + " no encontrado"));
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setNombreUser(user.getNombreUser());
        dto.setEmail(user.getEmail());
        dto.setTelefono(user.getTelefono());
        dto.setRol(user.getRol());
        dto.setEstado(user.getEstado());
        return dto;
    }

    @Transactional
    @Override
    public User save(User user) { //Crear cuenta
        if(this.userRepository.existeEmail(user.getEmail())){
            throw new RuntimeException("El correo electronico ya esta registrado");
        }
        user.setEstado("Active");
        log.info("Usuario guardado con exito");
        return this.userRepository.save(user);
    }

    @Transactional
    @Override
    public User desactiveById(Long id) { //Desactivar cuentas
        User user = this.userRepository.findById(id).orElseThrow(
                ()-> new UserException("User con ID " + id + " no encontrado"));
        user.setEstado("Inactive");
        log.info("Usuario desactivado con exito");
        return userRepository.save(user);
    }

    @Transactional
    @Override
    public User updateTelefono(Long id, String telefono) {//Updatear el telefono;
        return this.userRepository.findById(id).map(element ->{
            element.setTelefono(telefono);
            log.info("Telefono del usuario guardado con exito");
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
            log.info("Dirección del usuario guardado con exito");
            return this.userRepository.save(element);
        }).orElseThrow(
                ()  -> new UserException("Cuenta no encontrada, no se puede actualizar el telefono")
        );
    }
}
