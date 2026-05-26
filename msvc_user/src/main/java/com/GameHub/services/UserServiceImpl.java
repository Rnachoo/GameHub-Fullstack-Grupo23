package com.GameHub.services;

import com.GameHub.exceptions.UserException;
import com.GameHub.models.Direction;
import com.GameHub.models.User;
import com.GameHub.models.dtos.*;
import com.GameHub.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
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
    public List<UserDetalleDTO> findAll() {
        log.info("Listando usuarios registrados en el sistema!");
        return this.userRepository.findAll().stream().map(user -> {
            UserDetalleDTO dto = new UserDetalleDTO();
            dto.setId(user.getId());
            dto.setNombreUser(user.getNombreUser());
            dto.setEmail(user.getEmail());
            dto.setTelefono(user.getTelefono());
            dto.setRol(user.getRol());
            dto.setEstado(user.getEstado());

            List<DirectionDetalleDTO> directionDTO = user.getDirections().stream().map(dir ->{
                DirectionDetalleDTO dirDTO = new DirectionDetalleDTO();
                dirDTO.setComuna(dir.getComuna());
                dirDTO.setCiudad(dir.getCiudad());
                dirDTO.setCalle(dir.getCalle());
                dirDTO.setNumero(dir.getNumero());
                return dirDTO;
            }).toList();
            dto.setDirectionsDTO(directionDTO);
            return dto;
        }).toList();
    };

    @Transactional(readOnly = true)
    @Override
    public List<UserDetalleDTO> findByRol(String rol) {//Filtra por el rol del user
        log.info("Listando usuarios registrados en el sistema!");
        return this.userRepository.findByRol(rol).stream().map(user -> {
            UserDetalleDTO dto = new UserDetalleDTO();
            dto.setId(user.getId());
            dto.setNombreUser(user.getNombreUser());
            dto.setEmail(user.getEmail());
            dto.setTelefono(user.getTelefono());
            dto.setRol(user.getRol());
            dto.setEstado(user.getEstado());

            List<DirectionDetalleDTO> directionDTO = user.getDirections().stream().map(dir ->{
                DirectionDetalleDTO dirDTO = new DirectionDetalleDTO();
                dirDTO.setComuna(dir.getComuna());
                dirDTO.setCiudad(dir.getCiudad());
                dirDTO.setCalle(dir.getCalle());
                dirDTO.setNumero(dir.getNumero());
                return dirDTO;
            }).toList();
            dto.setDirectionsDTO(directionDTO);
            return dto;
        }).toList();
    }
    @Transactional(readOnly = true)
    @Override
    public List<UserDetalleDTO> findByEmail(String email) {//Filtra por el email del user
        log.info("Listando usuarios registrados en el sistema!");
        return this.userRepository.findByEmail(email).stream().map(user -> {
            UserDetalleDTO dto = new UserDetalleDTO();
            dto.setId(user.getId());
            dto.setNombreUser(user.getNombreUser());
            dto.setEmail(user.getEmail());
            dto.setTelefono(user.getTelefono());
            dto.setRol(user.getRol());
            dto.setEstado(user.getEstado());

            List<DirectionDetalleDTO> directionDTO = user.getDirections().stream().map(dir ->{
                DirectionDetalleDTO dirDTO = new DirectionDetalleDTO();
                dirDTO.setComuna(dir.getComuna());
                dirDTO.setCiudad(dir.getCiudad());
                dirDTO.setCalle(dir.getCalle());
                dirDTO.setNumero(dir.getNumero());
                return dirDTO;
            }).toList();
            dto.setDirectionsDTO(directionDTO);
            return dto;
        }).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDetalleDTO> findByEstado(String estado){//Filtra por el estado del user
        log.info("Listando usuarios registrados en el sistema!");
        return this.userRepository.findByEstado(estado).stream().map(user -> {
            UserDetalleDTO dto = new UserDetalleDTO();
            dto.setId(user.getId());
            dto.setNombreUser(user.getNombreUser());
            dto.setEmail(user.getEmail());
            dto.setTelefono(user.getTelefono());
            dto.setRol(user.getRol());
            dto.setEstado(user.getEstado());

            List<DirectionDetalleDTO> directionDTO = user.getDirections().stream().map(dir ->{
                DirectionDetalleDTO dirDTO = new DirectionDetalleDTO();
                dirDTO.setComuna(dir.getComuna());
                dirDTO.setCiudad(dir.getCiudad());
                dirDTO.setCalle(dir.getCalle());
                dirDTO.setNumero(dir.getNumero());
                return dirDTO;
            }).toList();
            dto.setDirectionsDTO(directionDTO);
            return dto;
        }).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public UserDetalleDTO findById(Long id) {
        log.info("Buscando usuarios registrados en el sistema!");
        User user = this.userRepository.findById(id).orElseThrow(
                ()-> new UserException("User con ID " + id + " no encontrado"));
        UserDetalleDTO dto = new UserDetalleDTO();
        dto.setId(user.getId());
        dto.setNombreUser(user.getNombreUser());
        dto.setEmail(user.getEmail());
        dto.setTelefono(user.getTelefono());
        dto.setRol(user.getRol());
        dto.setEstado(user.getEstado());

        List<DirectionDetalleDTO> directionDTO = user.getDirections().stream().map(dir ->{
            DirectionDetalleDTO dirDTO = new DirectionDetalleDTO();
            dirDTO.setComuna(dir.getComuna());
            dirDTO.setCiudad(dir.getCiudad());
            dirDTO.setCalle(dir.getCalle());
            dirDTO.setNumero(dir.getNumero());
            return dirDTO;
        }).toList();
        dto.setDirectionsDTO(directionDTO);
        return dto;
    }

    @Transactional
    @Override
    public UserDetalleDTO save(UserSaveDTO userSaveDTO) { //Crear cuenta
        if(this.userRepository.existsByEmail(userSaveDTO.getEmail())){
            throw new UserException("El correo electronico ya esta registrado");
        }
        User user = new User();
        user.setNombreUser(userSaveDTO.getNombreUser());
        user.setEmail(userSaveDTO.getEmail());
        user.setTelefono(userSaveDTO.getTelefono());
        user.setRol(userSaveDTO.getRol());
        user.setEstado("Active");

        if(userSaveDTO.getDirectionsDTO() != null){
            List<Direction> directions = userSaveDTO.getDirectionsDTO().stream().map(dirDTO ->{
                Direction direction = new Direction();
                direction.setComuna(dirDTO.getComuna());
                direction.setCiudad(dirDTO.getCiudad());
                direction.setCalle(dirDTO.getCalle());
                direction.setNumero(dirDTO.getNumero());

                direction.setUser(user);
                return direction;
            }).toList();
            user.setDirections(directions);
        }
        User userSave = this.userRepository.save(user);
        log.info("Usuario con email "+userSave.getEmail()+" guardado con exito");

        UserDetalleDTO dto = new UserDetalleDTO();
        dto.setId(userSave.getId());
        dto.setNombreUser(userSave.getNombreUser());
        dto.setEmail(userSave.getEmail());
        dto.setTelefono(userSave.getTelefono());
        dto.setRol(userSave.getRol());
        dto.setEstado(userSave.getEstado());

        List<DirectionDetalleDTO> directionDTOs = userSave.getDirections().stream().map(dir -> {
            DirectionDetalleDTO ddto = new DirectionDetalleDTO();
            ddto.setComuna(dir.getComuna());
            ddto.setCiudad(dir.getCiudad());
            ddto.setCalle(dir.getCalle());
            ddto.setNumero(dir.getNumero());
            return ddto;
        }).toList();
        dto.setDirectionsDTO(directionDTOs);
        return dto;
    }

    @Transactional
    @Override
    public UserDetalleDTO desactiveById(Long id) { //Desactivar cuentas
        User user = this.userRepository.findById(id).orElseThrow(
                ()-> new UserException("User con ID " + id + " no encontrado"));
        user.setEstado("Inactive");
        user = userRepository.save(user);
        log.info("Usuario desactivado con exito");

        UserDetalleDTO dto = new UserDetalleDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setRol(user.getRol());
        dto.setEstado(user.getEstado());
        dto.setTelefono(user.getTelefono());

        List<DirectionDetalleDTO> directionDTO = user.getDirections().stream().map(dir ->{
            DirectionDetalleDTO dirDTO = new DirectionDetalleDTO();
            dirDTO.setComuna(dir.getComuna());
            dirDTO.setCiudad(dir.getCiudad());
            dirDTO.setCalle(dir.getCalle());
            dirDTO.setNumero(dir.getNumero());
            return dirDTO;
        }).toList();
        dto.setDirectionsDTO(directionDTO);
        return dto;
    }

    @Transactional
    @Override
    public UserDetalleDTO updateTelefono(Long id, UserUpdateTelefonoDTO telefonoDTO) {//Updatear el telefono;
        return this.userRepository.findById(id).map(user ->{
            user.setTelefono(telefonoDTO.getTelefono());
            log.info("Telefono del usuario guardado con exito");

            user = this.userRepository.save(user);

            UserDetalleDTO dto = new UserDetalleDTO();
            dto.setId(user.getId());
            dto.setEmail(user.getEmail());
            dto.setRol(user.getRol());
            dto.setEstado(user.getEstado());
            dto.setTelefono(user.getTelefono());

            List<DirectionDetalleDTO> directionDTO = user.getDirections().stream().map(dir ->{
                DirectionDetalleDTO dirDTO = new DirectionDetalleDTO();
                dirDTO.setComuna(dir.getComuna());
                dirDTO.setCiudad(dir.getCiudad());
                dirDTO.setCalle(dir.getCalle());
                dirDTO.setNumero(dir.getNumero());
                return dirDTO;
            }).toList();
            dto.setDirectionsDTO(directionDTO);
            return dto;

        }).orElseThrow(
                ()  -> new UserException("Cuenta no encontrada, no se puede actualizar el telefono")
        );
    }

    @Transactional
    @Override
    public UserDetalleDTO updateDirection(Long id, UserUpdateDirectionDTO directionDTO) {
        return this.userRepository.findById(id).map(user -> {
            Direction direction = new Direction();
            direction.setComuna(directionDTO.getComuna());
            direction.setCiudad(directionDTO.getCiudad());
            direction.setCalle(directionDTO.getCalle());
            direction.setNumero(directionDTO.getNumero());

            direction.setUser(user);
            user.getDirections().add(direction);
            user = this.userRepository.save(user);
            log.info("Dirección del usuario guardada con exito");

            UserDetalleDTO dto = new UserDetalleDTO();
            dto.setId(user.getId());
            dto.setEmail(user.getEmail());
            dto.setRol(user.getRol());
            dto.setEstado(user.getEstado());
            dto.setTelefono(user.getTelefono());

            List<DirectionDetalleDTO> directionDetalleDTO = user.getDirections().stream().map(dir ->{
                DirectionDetalleDTO dirDTO = new DirectionDetalleDTO();
                dirDTO.setComuna(dir.getComuna());
                dirDTO.setCiudad(dir.getCiudad());
                dirDTO.setCalle(dir.getCalle());
                dirDTO.setNumero(dir.getNumero());
                return dirDTO;
            }).toList();
            dto.setDirectionsDTO(directionDetalleDTO);
            return dto;

        }).orElseThrow(
                ()  -> new UserException("Cuenta no encontrada, no se puede actualizar la dirección")
        );
    }
}
