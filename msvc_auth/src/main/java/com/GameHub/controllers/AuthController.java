package com.GameHub.controllers;

import com.GameHub.models.Auth;
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
    public ResponseEntity<List<Auth>> findAll(){
        return ResponseEntity.status(HttpStatus.OK).body(authService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Auth> findById(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(authService.findById(id));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Auth> findByEmail(@PathVariable String email){
        return ResponseEntity.status(HttpStatus.OK).body(authService.findByEmail(email));
    }

    @PostMapping
    public ResponseEntity<Auth> save(@Valid @RequestBody Auth auth){
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.save(auth));
    }

    @PatchMapping ("/{id}")
    public ResponseEntity <Auth> desactiveById(@PathVariable Long id){
        Auth auth = authService.desactiveById(id);
        return ResponseEntity.ok(auth);

    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<Auth> updatePassword  (@PathVariable Long id, @Valid @RequestBody Auth auth){
        return ResponseEntity.status(HttpStatus.OK).body(authService.updatePassword(id, auth));
    }

    @PatchMapping("/{id}/rol")
    public ResponseEntity<Auth> updateRol (@PathVariable Long id, @RequestBody Auth auth){
        return ResponseEntity.status(HttpStatus.OK).body(authService.updateRol(id, auth));
    }

}
