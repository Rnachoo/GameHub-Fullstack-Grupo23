package com.GameHub.clients;

import com.GameHub.models.dtos.ProductDTO;
import com.GameHub.models.dtos.PromotionDTO;
import com.GameHub.models.dtos.PromotionSaveDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "msvc-promotion", url = "localhost:8009/api/v1/promociones")
public interface PromotionClient {

    @GetMapping("/{id}")
    PromotionDTO getPromotionById(@PathVariable("id") Long id);

    @PostMapping("/calcular")
    Long calcularDescuento(@RequestBody PromotionSaveDTO promotionSaveDTO);
}

