package com.GameHub.controllers;

import com.GameHub.models.dtos.UserDetalleDTO;
import com.GameHub.models.dtos.UserSaveDTO;
import com.GameHub.models.dtos.UserUpdateDirectionDTO;
import com.GameHub.models.dtos.UserUpdateTelefonoDTO;
import com.GameHub.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@Validated
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<UserDetalleDTO>> findAll(){
        return ResponseEntity.status(HttpStatus.OK).body(userService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDetalleDTO> findById(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(userService.findById(id));
    }

    @GetMapping("/rol/{rol}")
    public ResponseEntity<List<UserDetalleDTO>> findByRol(@PathVariable String rol){
        return ResponseEntity.status(HttpStatus.OK).body(userService.findByRol(rol));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<UserDetalleDTO>> findByEstado(@PathVariable String estado){
        return ResponseEntity.status(HttpStatus.OK).body(userService.findByEstado(estado));
    }

    @PostMapping
    public ResponseEntity<UserDetalleDTO> save(@Valid @RequestBody UserSaveDTO userSaveDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.save(userSaveDTO));

    }
    @PatchMapping ("/{id}")
    public ResponseEntity <UserDetalleDTO> desactiveById(@PathVariable Long id){
        UserDetalleDTO user = userService.desactiveById(id);
        return ResponseEntity.ok(user);

    }

    @PatchMapping("/{id}/telefono")
    public ResponseEntity<UserDetalleDTO> updateTelefono(@PathVariable Long id, @Valid @RequestBody UserUpdateTelefonoDTO telefonoDTO){
        return ResponseEntity.status(HttpStatus.OK).body(userService.updateTelefono(id, telefonoDTO));
    }

    @PatchMapping("/{id}/directions")
    public ResponseEntity<UserDetalleDTO> updateDirection (@PathVariable Long id, @Valid @RequestBody UserUpdateDirectionDTO directionDTO){
        return ResponseEntity.status(HttpStatus.OK).body(userService.updateDirection(id, directionDTO));
    }
}
