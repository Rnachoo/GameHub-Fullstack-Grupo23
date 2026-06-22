package com.GameHub.assemblers;

import com.GameHub.controllers.ReviewController;
import com.GameHub.models.dtos.ResenaResponseDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ReviewModelAssembler implements RepresentationModelAssembler<ResenaResponseDTO, EntityModel<ResenaResponseDTO>> {

    @Override
    public EntityModel<ResenaResponseDTO> toModel(ResenaResponseDTO resena) {
        return EntityModel.of(
                resena,
                linkTo(methodOn(ReviewController.class).findById(resena.getId())).withSelfRel(),
                linkTo(methodOn(ReviewController.class).findByProductoId(resena.getProductoId())).withRel("resenas-producto"),
                linkTo(methodOn(ReviewController.class).findByUsuarioId(resena.getUsuarioId())).withRel("resenas-usuario"),
                linkTo(methodOn(ReviewController.class).moderar(resena.getId())).withRel("moderar"),
                linkTo(methodOn(ReviewController.class).update(resena.getId(), null)).withRel("update")
        );
    }
}