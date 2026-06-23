package com.category.assemblers;

import com.category.controllers.CategoryController;
import com.category.models.dtos.CategoryDetalleDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
@Component
public class CategoryModelAssembler implements RepresentationModelAssembler <CategoryDetalleDTO, EntityModel<CategoryDetalleDTO>> {
    @Override
    public EntityModel<CategoryDetalleDTO> toModel(CategoryDetalleDTO category) {
        return EntityModel.of(
                category,
                linkTo(methodOn(CategoryController.class).findById(category.getId())).withSelfRel(),
                linkTo(methodOn(CategoryController.class).findAll()).withRel("categories"),
                linkTo(methodOn(CategoryController.class).desactiveByID(category.getId())).withRel("desactive"),
                linkTo(methodOn(CategoryController.class).updateNombre(category.getId(), null)).withRel("update-nombre"),
                linkTo(methodOn(CategoryController.class).updateDescripcion(category.getId(), null)).withRel("update-descripcion")
        );
    }
}
