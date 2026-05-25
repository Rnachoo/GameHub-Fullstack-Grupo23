package com.GameHub.clients;

import com.GameHub.clients.dtos.ProductoClientDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "msvc-product", url = "http://localhost:8091/api/v1/productos")
public interface ProductFeignClient {

    @GetMapping("/{id}")
    ProductoClientDTO getProductoById(@PathVariable("id") Long id);
}