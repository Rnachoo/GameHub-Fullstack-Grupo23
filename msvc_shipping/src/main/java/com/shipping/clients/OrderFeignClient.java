package com.shipping.clients;

import com.shipping.clients.dtos.OrdenClientDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "msvc-order", url = "http://localhost:8001/api/v1/ordenes")
public interface OrderFeignClient {

    @GetMapping("/id/{id}")
    OrdenClientDTO getOrdenById(@PathVariable("id") Long id);
}