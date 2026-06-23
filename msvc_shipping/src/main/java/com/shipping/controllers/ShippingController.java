package com.shipping.controllers;

import com.shipping.assemblers.ShippingModelAssembler;
import com.shipping.models.dtos.DespachoRequestDTO;
import com.shipping.models.dtos.DespachoResponseDTO;
import com.shipping.services.ShippingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/despachos")
@Validated
@Tag(name = "Despachos V1", description = "Métodos CRUD para la gestión de despachos y envíos")
public class ShippingController {

    @Autowired
    private ShippingService shippingService;

    @Autowired
    private ShippingModelAssembler shippingModelAssembler;

    @GetMapping
    @Operation(summary = "Listado de todos los despachos", description = "Devuelve una lista con todos los despachos registrados")
    @ApiResponse(responseCode = "200", description = "Operación exitosa")
    public ResponseEntity<CollectionModel<EntityModel<DespachoResponseDTO>>> findAll() {
        List<EntityModel<DespachoResponseDTO>> entityModels = shippingService.getAllDespachos()
                .stream()
                .map(shippingModelAssembler::toModel)
                .toList();
        CollectionModel<EntityModel<DespachoResponseDTO>> collectionModel = CollectionModel.of(
                entityModels,
                linkTo(methodOn(ShippingController.class).findAll()).withSelfRel()
        );
        return ResponseEntity.status(HttpStatus.OK).body(collectionModel);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar despacho por ID", description = "Devuelve los detalles de un despacho específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Despacho encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DespachoResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Despacho no encontrado")
    })
    public ResponseEntity<EntityModel<DespachoResponseDTO>> findById(
            @Parameter(description = "ID del despacho a buscar", required = true, example = "1")
            @PathVariable Long id) {
        EntityModel<DespachoResponseDTO> entityModel = shippingModelAssembler.toModel(shippingService.getDespachoById(id));
        return ResponseEntity.status(HttpStatus.OK).body(entityModel);
    }

    @GetMapping("/orden/{ordenId}")
    @Operation(summary = "Buscar despachos por orden", description = "Devuelve todos los despachos asociados a una orden")
    @ApiResponse(responseCode = "200", description = "Operación exitosa")
    public ResponseEntity<CollectionModel<EntityModel<DespachoResponseDTO>>> findByOrdenId(
            @Parameter(description = "ID de la orden", required = true, example = "1")
            @PathVariable Long ordenId) {
        List<EntityModel<DespachoResponseDTO>> entityModels = shippingService.getDespachosByOrdenId(ordenId)
                .stream()
                .map(shippingModelAssembler::toModel)
                .toList();
        CollectionModel<EntityModel<DespachoResponseDTO>> collectionModel = CollectionModel.of(
                entityModels,
                linkTo(methodOn(ShippingController.class).findByOrdenId(ordenId)).withSelfRel()
        );
        return ResponseEntity.status(HttpStatus.OK).body(collectionModel);
    }

    @GetMapping("/estado/{estado}")
    @Operation(summary = "Buscar despachos por estado", description = "Devuelve todos los despachos con un estado específico")
    @ApiResponse(responseCode = "200", description = "Operación exitosa")
    public ResponseEntity<CollectionModel<EntityModel<DespachoResponseDTO>>> findByEstado(
            @Parameter(description = "Estado del despacho", required = true, example = "PENDIENTE")
            @PathVariable String estado) {
        List<EntityModel<DespachoResponseDTO>> entityModels = shippingService.getDespachosByEstado(estado)
                .stream()
                .map(shippingModelAssembler::toModel)
                .toList();
        CollectionModel<EntityModel<DespachoResponseDTO>> collectionModel = CollectionModel.of(
                entityModels,
                linkTo(methodOn(ShippingController.class).findByEstado(estado)).withSelfRel()
        );
        return ResponseEntity.status(HttpStatus.OK).body(collectionModel);
    }

    @PostMapping
    @Operation(summary = "Crear un despacho", description = "Registra un nuevo despacho en el sistema")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos del despacho a crear", required = true,
            content = @Content(schema = @Schema(implementation = DespachoRequestDTO.class))
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Despacho creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<EntityModel<DespachoResponseDTO>> save(@Valid @RequestBody DespachoRequestDTO requestDTO) {
        EntityModel<DespachoResponseDTO> entityModel = shippingModelAssembler.toModel(shippingService.createDespacho(requestDTO));
        return ResponseEntity.status(HttpStatus.CREATED).body(entityModel);
    }

    @PatchMapping("/{id}/estado")
    @Operation(summary = "Actualizar estado del despacho", description = "Actualiza el estado, tracking y fecha de entrega de un despacho")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Despacho no encontrado")
    })
    public ResponseEntity<EntityModel<DespachoResponseDTO>> updateEstado(
            @Parameter(description = "ID del despacho", required = true, example = "1")
            @PathVariable Long id,
            @RequestParam String estado,
            @RequestParam(required = false) String tracking,
            @RequestParam(required = false) LocalDateTime fechaEntrega) {
        EntityModel<DespachoResponseDTO> entityModel = shippingModelAssembler.toModel(
                shippingService.updateEstadoDespacho(id, estado, tracking, fechaEntrega));
        return ResponseEntity.status(HttpStatus.OK).body(entityModel);
    }

    @PatchMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar un despacho", description = "Cancela un despacho registrado en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Despacho cancelado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Despacho no encontrado")
    })
    public ResponseEntity<EntityModel<DespachoResponseDTO>> cancelar(
            @Parameter(description = "ID del despacho a cancelar", required = true, example = "1")
            @PathVariable Long id) {
        EntityModel<DespachoResponseDTO> entityModel = shippingModelAssembler.toModel(shippingService.cancelarDespacho(id));
        return ResponseEntity.status(HttpStatus.OK).body(entityModel);
    }
}