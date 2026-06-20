package com.GameHub.assemblers;

import com.GameHub.controllers.UserController;
import com.GameHub.models.dtos.UserDetalleDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserModelAssembler implements RepresentationModelAssembler<UserDetalleDTO, EntityModel<UserDetalleDTO>> {

    @Override
    public EntityModel<UserDetalleDTO> toModel(UserDetalleDTO user) {
        return EntityModel.of(
                user,
                linkTo(methodOn(UserController.class).findById(user.getId())).withSelfRel(),
                linkTo(methodOn(UserController.class).findAll()).withRel("users"),
                linkTo(methodOn(UserController.class).desactiveById(user.getId())).withRel("desactive"),
                linkTo(methodOn(UserController.class).updateTelefono(user.getId(), null)).withRel("update-telefono"),
                linkTo(methodOn(UserController.class).updateDirection(user.getId(), null)).withRel("add-direction")
        );
    }
}