package com.GameHub.controllers;

import com.GameHub.models.dtos.CategoryDetalleDTO;
import com.GameHub.models.dtos.CategorySaveDTO;
import com.GameHub.models.dtos.CategoryUpdateDescripcionDTO;
import com.GameHub.models.dtos.CategoryUpdateNombreDTO;
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
    public ResponseEntity<List<com.GameHub.models.dtos.CategoryDetalleDTO>> findAll(){
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<com.GameHub.models.dtos.CategoryDetalleDTO> findById(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.findById(id));
    }

    @PostMapping
    public ResponseEntity<CategoryDetalleDTO> save(@Valid @RequestBody CategorySaveDTO categorySaveDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.save(categorySaveDTO));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CategoryDetalleDTO> desactiveByID(@PathVariable Long id){
        CategoryDetalleDTO category = categoryService.desactiveById(id);
        return ResponseEntity.ok(category);
    }

    @PatchMapping("/{id}/nombreCategory")
    public ResponseEntity <CategoryDetalleDTO> updateNombre(@PathVariable Long id, @Valid @RequestBody CategoryUpdateNombreDTO nombreDTO){
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.updateNombre(id, nombreDTO));
    }

    @PatchMapping("/{id}/descripcion")
    public ResponseEntity <CategoryDetalleDTO> updateDescripcion(@PathVariable Long id, @Valid @RequestBody CategoryUpdateDescripcionDTO descripcionDTO){
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.updateDescripcion(id, descripcionDTO));
    }

}
