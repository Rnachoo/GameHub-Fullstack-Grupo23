package com.GameHub.services;

import com.GameHub.exceptions.CategoryException;
import com.GameHub.models.Category;
import com.GameHub.models.dtos.CategoryDetalleDTO;
import com.GameHub.models.dtos.CategorySaveDTO;
import com.GameHub.models.dtos.CategoryUpdateDescripcionDTO;
import com.GameHub.models.dtos.CategoryUpdateNombreDTO;
import com.GameHub.repositories.CategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService{
    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    @Override
    public List<CategoryDetalleDTO> findAll() {//Listar todas las cateogorias
        log.info("Abriendo Listado de categorias registradas en el sistema!");
        return this.categoryRepository.findAll().stream().map(category -> {
            com.GameHub.models.dtos.CategoryDetalleDTO dto = new com.GameHub.models.dtos.CategoryDetalleDTO();
            dto.setId(category.getId());
            dto.setNombreCategory(category.getNombreCategory());
            dto.setDescripcion(category.getDescripcion());
            dto.setEstado(category.getEstado());
        return dto;
        }).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public CategoryDetalleDTO findById(Long id) {
        log.info("Buscando categorias registradas en el sistema!");
        Category category = this.categoryRepository.findById(id).orElseThrow(
                () -> new CategoryException("Categoria con ID "+ id + " no encontrada"));
        com.GameHub.models.dtos.CategoryDetalleDTO dto = new com.GameHub.models.dtos.CategoryDetalleDTO();
        dto.setId(category.getId());
        dto.setNombreCategory(category.getNombreCategory());
        dto.setDescripcion(category.getDescripcion());
        dto.setEstado(category.getEstado());
        return dto;
    }

    @Transactional
    @Override
    public CategoryDetalleDTO save(CategorySaveDTO categorySaveDTO) {
        if(this.categoryRepository.existeNombre(categorySaveDTO.getNombreCategory())){
            throw new RuntimeException("Ya existe una categoria registrada con ese nombre");
        }
        Category category = new Category();
        category.setNombreCategory(categorySaveDTO.getNombreCategory());
        category.setDescripcion(categorySaveDTO.getDescripcion());
        category.setEstado("Active");
        category = categoryRepository.save(category);
        log.info("Categoria guardada con exito");
        CategoryDetalleDTO dto = new CategoryDetalleDTO();
        dto.setId(category.getId());
        dto.setNombreCategory(category.getNombreCategory());
        dto.setDescripcion(category.getDescripcion());
        dto.setId(category.getId());
        return dto;
    }

    @Transactional
    @Override
    public CategoryDetalleDTO desactiveById(Long id) { //Debe actualizarse, una ves creado el msvc Product
        Category category = this.categoryRepository.findById(id).orElseThrow(
                () -> new CategoryException("Categoria con ID "+ id +" no encontrada"));
        category.setEstado("Inactive");
        category = categoryRepository.save(category);
        log.info("Categoria con id "+ id +"Ha sido desactivada");
        CategoryDetalleDTO dto = new CategoryDetalleDTO();
        dto.setId(category.getId());
        dto.setNombreCategory(category.getNombreCategory());
        dto.setDescripcion(category.getDescripcion());
        dto.setId(category.getId());
        return dto;
    }

    @Transactional
    @Override
    public CategoryDetalleDTO updateNombre(Long id, CategoryUpdateNombreDTO nombreDTO) {
        return this.categoryRepository.findById(id).map(category ->{
            category.setNombreCategory(nombreDTO.getNombreCategory());
            log.info("Nombre de la categoria actualizadfo con exito");
            category = this.categoryRepository.save(category);

            CategoryDetalleDTO dto = new CategoryDetalleDTO();
            dto.setId(category.getId());
            dto.setNombreCategory(category.getNombreCategory());
            dto.setDescripcion(category.getDescripcion());
            dto.setId(category.getId());
            return dto;

        }).orElseThrow(
                () -> new CategoryException("Categoria no encontrada, no se puede actualizar el nombre")
        );
    }

    @Transactional
    @Override
    public CategoryDetalleDTO updateDescripcion(Long id, CategoryUpdateDescripcionDTO descripcionDTO) {
        return this.categoryRepository.findById(id).map(category ->{
            category.setDescripcion(descripcionDTO.getDescripcion());
            log.info("Descripcion de la categoría actualiazda con exito");
            category = this.categoryRepository.save(category);

            CategoryDetalleDTO dto = new CategoryDetalleDTO();
            dto.setId(category.getId());
            dto.setNombreCategory(category.getNombreCategory());
            dto.setDescripcion(category.getDescripcion());
            dto.setId(category.getId());
            return dto;


        }).orElseThrow(
                () -> new CategoryException("Categoria no encontrada, no se puede actualizar la descripcion")
        );
    }
}
