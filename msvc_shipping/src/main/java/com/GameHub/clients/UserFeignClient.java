package com.GameHub.clients;

import com.GameHub.clients.dtos.UsuarioClientDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "msvc-user", url = "${msvc.user.url}")
public interface UserFeignClient {

    @GetMapping("/api/v1/users/{id}")
    UsuarioClientDTO getUsuarioById(@PathVariable Long id);
}