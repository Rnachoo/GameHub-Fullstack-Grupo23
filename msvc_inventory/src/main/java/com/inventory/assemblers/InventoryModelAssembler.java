package com.inventory.assemblers;

import com.inventory.controllers.InventoryController;
import com.inventory.models.dtos.InventoryDetalleDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class InventoryModelAssembler implements RepresentationModelAssembler<InventoryDetalleDTO, EntityModel<InventoryDetalleDTO>> {

    @Override
    public EntityModel<InventoryDetalleDTO> toModel(InventoryDetalleDTO inventory) {
        return EntityModel.of(
                inventory,
                linkTo(methodOn(InventoryController.class).findById(inventory.getId())).withSelfRel(),
                linkTo(methodOn(InventoryController.class).findAllByProduct(inventory.getProductId())).withRel("product-inventory"),
                linkTo(methodOn(InventoryController.class).updateCantidadDisponible(inventory.getId(), null)).withRel("update-stock"),
                linkTo(methodOn(InventoryController.class).reservarStock(inventory.getProductId(), null)).withRel("reservar"),
                linkTo(methodOn(InventoryController.class).liberarStock(inventory.getProductId(), null)).withRel("liberar"),
                linkTo(methodOn(InventoryController.class).confirmarVenta(inventory.getProductId(), null)).withRel("confirmar-venta")
        );
    }
}