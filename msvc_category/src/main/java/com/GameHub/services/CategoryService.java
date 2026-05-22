package com.GameHub.services;

import com.GameHub.models.Category;
import com.GameHub.models.dtos.CategoryDetalleDTO;
import com.GameHub.models.dtos.CategorySaveDTO;
import com.GameHub.models.dtos.CategoryUpdateDescripcionDTO;
import com.GameHub.models.dtos.CategoryUpdateNombreDTO;

import java.util.List;

public interface CategoryService {
    List<CategoryDetalleDTO> findAll();
    CategoryDetalleDTO findById(Long id);
    CategoryDetalleDTO save (CategorySaveDTO categorySaveDTO);
    CategoryDetalleDTO desactiveById(Long id);
    CategoryDetalleDTO updateNombre(Long id, CategoryUpdateNombreDTO nombreDTO);
    CategoryDetalleDTO updateDescripcion(Long id, CategoryUpdateDescripcionDTO descripcionDTO);

}
