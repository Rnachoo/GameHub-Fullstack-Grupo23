package com.order.clients;

import com.order.models.dtos.PromotionDTO;
import com.order.models.dtos.PromotionSaveDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "msvc-promotion", url = "http://localhost:8009/api/v1/promociones")
public interface PromotionClient {

    @PostMapping("/{codigo}/aplicar")
    PromotionDTO aplicarPromocion(
            @PathVariable("codigo") String codigo,
            @RequestBody PromotionSaveDTO promotionSaveDTO,
            @RequestParam("totalOrden") Double totalOrden);
}