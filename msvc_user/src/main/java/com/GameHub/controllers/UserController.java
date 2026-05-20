package com.GameHub.controllers;


import com.GameHub.models.Direction;
import com.GameHub.models.User;
import com.GameHub.models.dtos.UserDTO;
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
    public ResponseEntity<List<UserDTO>> findAll(){
        return ResponseEntity.status(HttpStatus.OK).body(userService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> findById(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(userService.findById(id));
    }

    @GetMapping("/rol/{rol}")
    public ResponseEntity<List<UserDTO>> findByRol(@PathVariable String rol){
        return ResponseEntity.status(HttpStatus.OK).body(userService.findByRol(rol));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<UserDTO>> findByEstado(@PathVariable String estado){
        return ResponseEntity.status(HttpStatus.OK).body(userService.findByEstado(estado));
    }

    @PostMapping
    public ResponseEntity<User> save(@Valid @RequestBody User user){
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.save(user));

    }
    @PatchMapping ("/{id}")
    public ResponseEntity <User> desactiveById(@PathVariable Long id){
        User user = userService.desactiveById(id);
        return ResponseEntity.ok(user);

    }

    @PatchMapping("/{id}/telefono")
    public ResponseEntity<User> updateTelefono  (@PathVariable Long id, @Valid @RequestBody String telefono){
        return ResponseEntity.status(HttpStatus.OK).body(userService.updateTelefono(id, telefono));
    }

    @PatchMapping("/{id}/direction")
    public ResponseEntity<User> updateDirection (@PathVariable Long id, @RequestBody Direction direction){
        return ResponseEntity.status(HttpStatus.OK).body(userService.updateDirection(id, direction));
    }
}
