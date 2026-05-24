package com.GameHub.clients;

import com.GameHub.models.dtos.InventoryDTO;
import com.GameHub.models.dtos.ProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "msvc-inventory", url = "localhost:8005/api/productos")
public interface InventoryClient {

    @PutMapping("/producto/{productId}/reservar")
    InventoryDTO reservarStock(@PathVariable("productId") Long productId, @RequestBody CantidadStockDTO cantidadDTO);
}

