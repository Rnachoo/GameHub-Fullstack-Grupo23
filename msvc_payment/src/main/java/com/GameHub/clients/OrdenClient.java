package com.GameHub.clients;

import com.GameHub.models.dtos.OrdenDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "msvc-orden", url = "localhost:8001/api/ordenes")
public interface OrdenClient {
    @GetMapping("/id/{id}")
    OrdenDTO getOrdenById(@PathVariable("id") Long id);
}
