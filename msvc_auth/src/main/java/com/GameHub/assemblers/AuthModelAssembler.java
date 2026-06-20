package com.GameHub.assemblers;

import com.GameHub.controllers.AuthController;
import com.GameHub.models.dtos.AuthDetalleDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class AuthModelAssembler implements RepresentationModelAssembler<AuthDetalleDTO, EntityModel<AuthDetalleDTO>> {

    @Override
    public EntityModel<AuthDetalleDTO> toModel(AuthDetalleDTO auth) {
        return EntityModel.of(
                auth,
                linkTo(methodOn(AuthController.class).findById(auth.getId())).withSelfRel(),
                linkTo(methodOn(AuthController.class).findAll()).withRel("auths"),
                linkTo(methodOn(AuthController.class).desactiveById(auth.getId())).withRel("desactive"),
                linkTo(methodOn(AuthController.class).updatePassword(auth.getId(), null)).withRel("update-password"),
                linkTo(methodOn(AuthController.class).updateRol(auth.getId(), null)).withRel("update-rol")
        );
    }
}