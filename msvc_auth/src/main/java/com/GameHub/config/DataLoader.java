package com.GameHub.config;

import com.GameHub.models.Rol;
import com.GameHub.repositories.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private RolRepository rolRepository;

    @Override
    public void run(String... args) throws Exception {
        // Crear ROLE_USER si no existe
        if (rolRepository.findByNombre("ROLE_USER").isEmpty()) {
            Rol userRol = new Rol();
            userRol.setNombre("ROLE_USER");
            rolRepository.save(userRol);
        }

        // Crear ROLE_ADMIN si no existe
        if (rolRepository.findByNombre("ROLE_ADMIN").isEmpty()) {
            Rol adminRol = new Rol();
            adminRol.setNombre("ROLE_ADMIN");
            rolRepository.save(adminRol);
        }
    }
}