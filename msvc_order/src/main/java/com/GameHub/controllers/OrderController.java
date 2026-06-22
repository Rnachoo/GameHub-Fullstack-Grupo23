package com.GameHub.controllers;

import com.GameHub.assemblers.OrderModelAssembler;
import com.GameHub.models.dtos.OrderDetalleDTO;
import com.GameHub.models.dtos.OrderSaveDTO;
import com.GameHub.models.dtos.OrderUpdateEstadoDTO;
import com.GameHub.services.OrderService;
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

import java.util.List;

@RestController
@RequestMapping("/api/v1/ordenes")
@Validated
@Tag(name = "Ordenes V1", description = "Métodos Crud para la gestión de ordenes")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderModelAssembler orderModelAssembler;

    //Encontrar una orden por la ID de un usuario
    @GetMapping("/cliente/{userId}")
    @Operation(summary = "Listar Ordenes por cliente", description = "Devuelve el historial de ordenes realizadas por un usuario encontrado por el ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Operación exitosa",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = OrderDetalleDTO.class)))
    })
    public ResponseEntity<CollectionModel<EntityModel<OrderDetalleDTO>>> findByClient(
            @Parameter(description = "ID del usuario (cliente)", required = true,example = "1")
            @PathVariable Long userId
    ){
        List<EntityModel<OrderDetalleDTO>> entityModels = this.orderService.findByClient(userId)
                .stream()
                .map(orderModelAssembler::toModel)
                .toList();
        CollectionModel<EntityModel<OrderDetalleDTO>> collectionModel = CollectionModel.of(entityModels);
        return ResponseEntity.status(HttpStatus.OK).body(collectionModel);

    }

    //Encontrar una orden por el Estado
    @GetMapping("/estado/{estado}")
    @Operation(summary = "Listar Ordenes por Estado de la Orden",
            description = "Devuelve una lista de ordenes según su estado")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Operación exitosa",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderDetalleDTO.class)))
    })
    public ResponseEntity<CollectionModel<EntityModel<OrderDetalleDTO>>> findByEstado(
            @Parameter(description = "Estado de la Orden", required = true, example = "PENDIENTE_PAGO")
            @PathVariable String estado
    ){
        List<EntityModel<OrderDetalleDTO>> entityModels = this.orderService.findByEstado(estado)
                .stream()
                .map(orderModelAssembler::toModel)
                .toList();
        CollectionModel<EntityModel<OrderDetalleDTO>> collectionModel = CollectionModel.of(entityModels);
        return ResponseEntity.status(HttpStatus.OK).body(collectionModel);
    }

    //Encontrar una orden por el ID
    @GetMapping("/{id}")
    @Operation(summary = "Buscar Orden por ID",
            description = "Devuelve el detalle completo de una orden específica")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Orden encontrada",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = OrderDetalleDTO.class))),
            @ApiResponse(responseCode = "404",
                    description = "Orden no encontrada en el sistema")
    })
    public ResponseEntity<EntityModel<OrderDetalleDTO>> findById(
            @Parameter(description = "ID de la orden", required = true, example = "1")
            @PathVariable Long id
    ){
        EntityModel<OrderDetalleDTO> entityModel = this.orderModelAssembler.toModel(orderService.findById(id));
        return ResponseEntity.status(HttpStatus.OK).body(entityModel);
    }


    //Registrar una Orden
    @PostMapping
    @Operation(summary = "Crear nueva Orden",
            description = "Orquesta la validación de usuario, disponibilidad de stock, descuentos y genera la orden de cobro")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Orden creada con éxito",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = OrderDetalleDTO.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Stock insuficiente, cupón inválido o datos erróneos"),
            @ApiResponse(
                    responseCode = "404",
                    description = "El usuario o alguno de los productos no existe")
    })
    public ResponseEntity<EntityModel<OrderDetalleDTO>> save(@Valid @RequestBody OrderSaveDTO orderSaveDTO) {
        OrderDetalleDTO orderCreate = orderService.save(orderSaveDTO);
        EntityModel<OrderDetalleDTO> entityModel = this.orderModelAssembler.toModel(orderCreate);
        return ResponseEntity.status(HttpStatus.CREATED).body(entityModel);
    }

    //Actualiza el estado de una orden
    @PutMapping("/{id}/estado")
    @Operation(summary = "Actualizar Estado de Orden",
            description = "Modifica el ciclo de vida de la orden")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Estado actualizado con éxito",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = OrderDetalleDTO.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Orden no encontrada")
    })
    public ResponseEntity<EntityModel<OrderDetalleDTO>> updateEstado(
            @Parameter(description = "ID de la orden", required = true, example = "1")
            @PathVariable Long id, @Valid @RequestBody OrderUpdateEstadoDTO orderUpdateEstadoDTO
    ){
        OrderDetalleDTO orderUpdate = orderService.updateEstado(id, orderUpdateEstadoDTO);
        EntityModel<OrderDetalleDTO> entityModel = this.orderModelAssembler.toModel(orderUpdate);
        return ResponseEntity.status(HttpStatus.OK).body(entityModel);
    }


    //Cancela una orden
    @PatchMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar Orden", description = "Cambia el estado a CANCELADA y libera el stock reservado en el inventario")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Orden cancelada exitosamente sin contenido"),
            @ApiResponse(
                    responseCode = "400",
                    description = "La orden ya estaba cancelada"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Orden no encontrada")
    })
    public ResponseEntity<Void> cancelarOrden(
            @Parameter(description = "ID de la orden a cancelar", required = true, example = "1")
            @PathVariable Long id
    ){
        orderService.cancelarOrden(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

