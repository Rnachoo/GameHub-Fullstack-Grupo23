package com.GameHub.clients;

import com.GameHub.models.dtos.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "msvc-user", url = "localhost:8002/api/v1/users") // Corrección aquí
public interface UserClient {
    @GetMapping("/{id}")
    UserDTO getUserById(@PathVariable("id") Long id);
}