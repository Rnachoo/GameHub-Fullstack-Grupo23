package com.GameHub.clients;

import com.GameHub.clients.dtos.OrdenClientDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "msvc-order", url = "${msvc.order.url}")
public interface OrderFeignClient {
    @GetMapping("/api/v1/orders/{id}")
    OrdenClientDTO getOrdenById(@PathVariable("id") Long id);
}