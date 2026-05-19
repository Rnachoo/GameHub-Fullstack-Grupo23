package com.GameHub.services;

import com.GameHub.exceptions.CategoryException;
import com.GameHub.models.Category;
import com.GameHub.models.dtos.CategoryDetalleDTO;
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
            CategoryDetalleDTO dto = new CategoryDetalleDTO();
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
        CategoryDetalleDTO dto = new CategoryDetalleDTO();
        dto.setId(category.getId());
        dto.setNombreCategory(category.getNombreCategory());
        dto.setDescripcion(category.getDescripcion());
        dto.setEstado(category.getEstado());
        return dto;
    }

    @Transactional
    @Override
    public Category save(Category category) {
        if(this.categoryRepository.existeNombre(category.getNombreCategory())){
            throw new RuntimeException("Ya existe una categoria registrada con ese nombre");
        }
        category.setEstado("Active");
        log.info("Categoria guardada con exito");
        return this.categoryRepository.save(category);
    }

    @Override
    public Category desactiveById(Long id) { //Debe actualizarse, una ves creado el msvc Product
        Category category = this.categoryRepository.findById(id).orElseThrow(
                () -> new CategoryException("Categoria con ID "+ id +" no encontrada"));
        category.setEstado("Inactive");
        log.info("Categoria con id "+ id +"Ha sido desactivada");
        return categoryRepository.save(category);
    }

    @Override
    public Category updateNombre(Long id, String nombreCategory) {
        return this.categoryRepository.findById(id).map(element ->{
            element .setNombreCategory(nombreCategory);
            log.info("Nombre de la categoria actualizadfo con exito");
            return this.categoryRepository.save(element);
        }).orElseThrow(
                () -> new CategoryException("Categoria no encontrada, no se puede actualizar el nombre")
        );
    }

    @Override
    public Category updateDescripcion(Long id, String descripcion) {
        return this.categoryRepository.findById(id).map(element ->{
            element.setDescripcion(descripcion);
            log.info("Descripcion de la categoría actualiazda con exito");
            return this.categoryRepository.save(element);
        }).orElseThrow(
                () -> new CategoryException("Categoria no encontrada, no se puede actualizar la descripcion")
        );
    }
}
