package com.GameHub.clients;

import com.GameHub.models.dtos.ProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "msvc-user", url = "localhost:8002/api/inventarios")
public interface UserClient {

    @GetMapping("/{id}")
    ProductDTO getUserById(@PathVariable("id") Long id);
}

