package com.GameHub.assemblers;

import com.GameHub.controllers.PaymentController;
import com.GameHub.models.dtos.PaymentDetalleDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PaymentModelAssembler implements RepresentationModelAssembler<PaymentDetalleDTO, EntityModel<PaymentDetalleDTO>> {

    @Override
    public EntityModel<PaymentDetalleDTO> toModel(PaymentDetalleDTO payment) {
        return EntityModel.of(
                payment,
                linkTo(methodOn(PaymentController.class).findById(payment.getId())).withSelfRel(),
                linkTo(methodOn(PaymentController.class).findAllByOrdenId(payment.getOrdenId())).withRel("orden-pagos"),
                linkTo(methodOn(PaymentController.class).nullById(payment.getId())).withRel("anular-pago")
        );
    }
}
