package com.GameHub.clients;

import com.GameHub.clients.dtos.CategoryClientDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "msvc-category", url = "${msvc.category.url}")
public interface CategoryFeignClient {

    @GetMapping("/api/v1/categories/{id}")
    CategoryClientDTO getCategoryById(@PathVariable("id") Long id);
}