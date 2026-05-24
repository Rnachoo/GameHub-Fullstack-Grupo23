package com.GameHub.clients;

import com.GameHub.models.dtos.CategoryDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "msvc-category", url = "localhost:8004/api/categories")
public interface CategoryClient {
    @GetMapping("/id/{id}")
    CategoryDTO getCategoryById(@PathVariable("id") Long id);
}
