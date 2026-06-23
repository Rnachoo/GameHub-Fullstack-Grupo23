package com.review.clients;

import com.review.clients.dtos.UsuarioClientDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "msvc-user", url = "http://localhost:8002/api/v1/users")
public interface UserFeignClient {

    @GetMapping("/{id}")
    UsuarioClientDTO getUsuarioById(@PathVariable("id") Long id);
}