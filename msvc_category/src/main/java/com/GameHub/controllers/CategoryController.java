package com.GameHub.controllers;

import com.GameHub.models.Category;
import com.GameHub.models.dtos.CategoryDetalleDTO;
import com.GameHub.services.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@Validated
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryDetalleDTO>> findAll(){
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDetalleDTO> findByID(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Category> save(@Valid @RequestBody Category category){
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.save(category));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Category> desactiveByID(@PathVariable Long id){
        Category category = categoryService.desactiveById(id);
        return ResponseEntity.ok(category);
    }

    @PatchMapping("/{id}/nombreCategory")
    public ResponseEntity <Category> updateNombre(@PathVariable Long id, @Valid @RequestBody String nombreCategory){
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.updateNombre(id, nombreCategory));
    }

    @PatchMapping("/{id}/descripcion")
    public ResponseEntity <Category> updateDescripcion(@PathVariable Long id, @Valid @RequestBody String descripcion){
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.updateDescripcion(id, descripcion));
    }

}
