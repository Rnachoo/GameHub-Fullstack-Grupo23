package com.GameHub.repositories;

import com.GameHub.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existeEmail(String email);
    List<User> findByRol(String rol);
    List<User> findByEstado(String estado);
}
