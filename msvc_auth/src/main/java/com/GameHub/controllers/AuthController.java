package com.GameHub.controllers;

import com.GameHub.models.Auth;
import com.GameHub.models.dtos.*;
import com.GameHub.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auths")
@Validated
public class AuthController {
    @Autowired
    private AuthService authService;

    @GetMapping
    public ResponseEntity<List<AuthDetalleDTO>> findAll(){
        return ResponseEntity.status(HttpStatus.OK).body(authService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthDetalleDTO> findById(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(authService.findById(id));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<AuthDetalleDTO> findByEmail(@PathVariable String email){
        return ResponseEntity.status(HttpStatus.OK).body(authService.findByEmail(email));
    }

    @PostMapping
    public ResponseEntity<AuthDetalleDTO> save(@Valid @RequestBody AuthSaveDTO authSaveDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.save(authSaveDTO));
    }

    @PatchMapping ("/{id}")
    public ResponseEntity <AuthDetalleDTO> desactiveById(@PathVariable Long id){
        AuthDetalleDTO auth = authService.desactiveById(id);
        return ResponseEntity.ok(auth);

    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<AuthDetalleDTO> updatePassword  (@PathVariable Long id, @Valid @RequestBody AuthUpdatePasswordDTO passwordDTO){
        return ResponseEntity.status(HttpStatus.OK).body(authService.updatePassword(id, passwordDTO));
    }

    @PatchMapping("/{id}/rol")
    public ResponseEntity<AuthDetalleDTO> updateRol (@PathVariable Long id, @Valid @RequestBody AuthUpdateRolDTO rolDTO){
        return ResponseEntity.status(HttpStatus.OK).body(authService.updateRol(id, rolDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthDetalleDTO> login(@RequestBody AuthLoginDTO authLoginDTO) {
        AuthDetalleDTO response = authService.login(authLoginDTO);
        return ResponseEntity.ok(response);
    }
}
