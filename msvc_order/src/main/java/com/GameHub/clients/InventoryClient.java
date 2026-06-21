package com.GameHub.clients;

import com.GameHub.models.dtos.InventoryCantidadDTO;
import com.GameHub.models.dtos.InventoryDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "msvc-inventory", url = "http://localhost:8005/api/v1/inventories")
public interface InventoryClient {

    @PutMapping("/producto/{productId}/reservar")
    InventoryDTO reservarStock(@PathVariable("productId") Long productId, @RequestBody InventoryCantidadDTO cantidadDTO);

    @PutMapping("/producto/{productId}/liberar")
    InventoryDTO liberarStock(@PathVariable("productId") Long productId, @RequestBody InventoryCantidadDTO cantidadDTO);
}