package com.shipping.assemblers;

import com.shipping.controllers.ShippingController;
import com.shipping.models.dtos.DespachoResponseDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ShippingModelAssembler implements RepresentationModelAssembler<DespachoResponseDTO, EntityModel<DespachoResponseDTO>> {

    @Override
    public EntityModel<DespachoResponseDTO> toModel(DespachoResponseDTO despacho) {
        return EntityModel.of(
                despacho,
                linkTo(methodOn(ShippingController.class).findById(despacho.getId())).withSelfRel(),
                linkTo(methodOn(ShippingController.class).findAll()).withRel("despachos"),
                linkTo(methodOn(ShippingController.class).cancelar(despacho.getId())).withRel("cancelar"),
                linkTo(methodOn(ShippingController.class).updateEstado(despacho.getId(), null, null, null)).withRel("update-estado")
        );
    }
}