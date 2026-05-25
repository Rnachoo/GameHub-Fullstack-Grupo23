package com.GameHub.clients;

import com.GameHub.clients.dtos.CategoryClientDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "msvc-category", url = "http://localhost:8004/api/v1/categories")
public interface CategoryFeignClient {

    @GetMapping("/{id}")
    CategoryClientDTO getCategoryById(@PathVariable("id") Long id);
}