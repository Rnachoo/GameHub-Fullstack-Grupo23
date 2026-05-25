package com.GameHub.clients;

import com.GameHub.models.dtos.OrderDTO;
import com.GameHub.models.dtos.OrderUpdateEstadoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "msvc-orden", url = "localhost:8001/api/v1/ordenes")
public interface OrdenClient {
    @GetMapping("/id/{id}")
    OrderDTO getOrdenById(@PathVariable("id") Long id);

    @PatchMapping("/{id}/estado")
    OrderDTO actualizarEstadoOrden(@PathVariable("id") Long id, @RequestBody OrderUpdateEstadoDTO estadoDTO);
}
