package com.GameHub.clients;

import com.GameHub.clients.dtos.ProductoClientDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "msvc-product", url = "${msvc.product.url}")
public interface ProductFeignClient {
    @GetMapping("/api/v1/productos/{id}")
    ProductoClientDTO getProductoById(@PathVariable("id") Long id);
}