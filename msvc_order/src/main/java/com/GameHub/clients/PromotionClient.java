package com.GameHub.clients;

import com.GameHub.models.dtos.PromotionDTO;
import com.GameHub.models.dtos.PromotionSaveDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "msvc-promotion", url = "localhost:8009/api/v1/promociones")
public interface PromotionClient {

    @PostMapping("/{codigo}/aplicar")
    PromotionDTO aplicarPromocion(
            @PathVariable("codigo") String codigo, @RequestBody PromotionSaveDTO promotionSaveDTO, @RequestParam("totalOrden") Double totalOrden);
}

