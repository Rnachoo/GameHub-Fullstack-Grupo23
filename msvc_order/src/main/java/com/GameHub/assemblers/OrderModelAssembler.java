package com.GameHub.assemblers;

import com.GameHub.controllers.OrderController;
import com.GameHub.models.dtos.OrderDetalleDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class OrderModelAssembler implements RepresentationModelAssembler<OrderDetalleDTO, EntityModel<OrderDetalleDTO>> {

    @Override
    public EntityModel<OrderDetalleDTO> toModel(OrderDetalleDTO order) {
        return EntityModel.of(
                order,
                linkTo(methodOn(OrderController.class).findById(order.getId())).withSelfRel(),
                linkTo(methodOn(OrderController.class).findByClient(order.getUserId())).withRel("cliente-ordenes"),
                linkTo(methodOn(OrderController.class).updateEstado(order.getId(), null)).withRel("update-estado"),
                linkTo(methodOn(OrderController.class).cancelarOrden(order.getId())).withRel("cancelar-orden")
        );
    }
}