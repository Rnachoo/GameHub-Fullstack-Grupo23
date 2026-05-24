package com.GameHub.clients;

import com.GameHub.models.dtos.ProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "msvc-promotion", url = "localhost:8009/api/productos")
public interface PromotionClient {

    @GetMapping("/{id}")
    ProductDTO getPromotionById(@PathVariable("id") Long id);
}

