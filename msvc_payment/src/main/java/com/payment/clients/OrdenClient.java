package com.payment.clients;

import com.payment.models.dtos.OrderDTO;
import com.payment.models.dtos.OrderUpdateEstadoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "msvc-orden", url = "http://localhost:8001/api/v1/ordenes")
public interface OrdenClient {

    @GetMapping("/id/{id}")
    OrderDTO getOrdenById(@PathVariable("id") Long id);

    @PutMapping("/{id}/estado")
    OrderDTO actualizarEstadoOrden(@PathVariable("id") Long id, @RequestBody OrderUpdateEstadoDTO estadoDTO);
}
