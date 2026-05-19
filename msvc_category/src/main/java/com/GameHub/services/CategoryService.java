package com.GameHub.services;

import com.GameHub.models.Category;
import com.GameHub.models.dtos.CategoryDetalleDTO;

import java.util.List;

public interface CategoryService {
    List<CategoryDetalleDTO> findAll();
    CategoryDetalleDTO findById(Long id);
    Category save (Category category);
    Category desactiveById(Long id);
    Category updateNombre(Long id, String nombreCategory);
    Category updateDescripcion(Long id, String descripcion);

}
