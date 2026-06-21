package com.GameHub.controllers;

import com.GameHub.assemblers.PaymentModelAssembler;
import com.GameHub.models.dtos.PaymentDetalleDTO;
import com.GameHub.models.dtos.PaymentSaveDTO;
import com.GameHub.models.dtos.PaymentUpdateEstadoDTO;
import com.GameHub.services.PaymentService;
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
@RequestMapping("/api/v1/payments")
@Validated
@Tag(name = "Pagos V1", description = "Procesamiento de transacciones, validación de montos y comprobantes")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentModelAssembler paymentModelAssembler;

    //Buscar un Pago por el ID de una orden
    @GetMapping("/orden/{ordenId}")
    @Operation(summary = "Listar Pagos por Orden", description = "Devuelve el historial de transacciones asociadas al ID de una orden")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Operación exitosa",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PaymentDetalleDTO.class)))
    })
    public ResponseEntity<CollectionModel<EntityModel<PaymentDetalleDTO>>> findAllByOrdenId(
            @Parameter(description = "ID de la orden", required = true, example = "1")
            @PathVariable Long ordenId
    ){
        List<EntityModel<PaymentDetalleDTO>> entityModels = paymentService.findAllByOrdenId(ordenId)
                .stream()
                .map(paymentModelAssembler::toModel)
                .toList();
        CollectionModel<EntityModel<PaymentDetalleDTO>> collectionModel = CollectionModel.of(entityModels);
        return ResponseEntity.status(HttpStatus.OK).body(collectionModel);
    }


    //Buscar un Pago por el Estado
    @GetMapping("/estado/{estado}")
    @Operation(summary = "Listar Pagos por su Estado", description = "Devuelve el historial de transacciones asociadas a un Estado")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Operación exitosa",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PaymentDetalleDTO.class)))
    })
    public ResponseEntity<CollectionModel<EntityModel<PaymentDetalleDTO>>> findAllByEstado(
            @Parameter(description = "Estado del poago a buscar", required = true, example = "APROBADO")
            @PathVariable String estado
    ){
        List<EntityModel<PaymentDetalleDTO>> entityModels = paymentService.findAllByEstado(estado)
                .stream()
                .map(paymentModelAssembler::toModel)
                .toList();
        CollectionModel<EntityModel<PaymentDetalleDTO>> collectionModel = CollectionModel.of(entityModels);
        return ResponseEntity.status(HttpStatus.OK).body(collectionModel);
    }


    //Buscar un Pago por su ID
    @GetMapping("/{id}")
    @Operation(summary = "Buscar Pago por ID", description = "Devuelve los detalles de un comprobante de pago específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pago encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PaymentDetalleDTO.class))),
            @ApiResponse(responseCode = "404", description = "Pago no encontrado")
    })
    public ResponseEntity<EntityModel<PaymentDetalleDTO>> findById(
            @Parameter(description = "ID de un Pago", required = true, example = "1")
            @PathVariable Long id
    ){
        EntityModel<PaymentDetalleDTO> entityModel = paymentModelAssembler.toModel(paymentService.findById(id));
        return ResponseEntity.status(HttpStatus.OK).body(entityModel);
    }


    //Registrar un Pago
    @PostMapping
    @Operation(summary = "Procesar Pago", description = "Crea un registro de pago, genera un código de transacción único y actualiza el estado de la orden")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Objeto con los datos de la orden y monto a pagar", required = true,
            content = @Content(schema = @Schema(implementation = PaymentSaveDTO.class))
    )
    public ResponseEntity<EntityModel<PaymentDetalleDTO>> save (@Valid @RequestBody PaymentSaveDTO paymentSaveDTO){
        PaymentDetalleDTO paymentCreate = paymentService.save(paymentSaveDTO);
        EntityModel<PaymentDetalleDTO> entityModel = paymentModelAssembler.toModel(paymentCreate);
        return ResponseEntity.status(HttpStatus.CREATED).body(entityModel);
    }


    //Actualziar el Estado de un Pago
    @PatchMapping("/{id}/estado")
    @Operation(summary = "Actualizar Estado Manual", description = "Modifica manualmente el estado de un comprobante de pago")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Objeto con el nuevo estado a asignar al pago", required = true,
            content = @Content(schema = @Schema(implementation = PaymentUpdateEstadoDTO.class)))
    public ResponseEntity<EntityModel<PaymentDetalleDTO>> updateEstado (
            @Parameter(description = "ID de un Pago", required = true, example = "1")
            @PathVariable Long id, @Valid @RequestBody PaymentUpdateEstadoDTO paymentUpdateEstadoDTO
    ){
        PaymentDetalleDTO paymentUpdate = paymentService.updateEstado(id, paymentUpdateEstadoDTO);
        EntityModel<PaymentDetalleDTO> entityModel = paymentModelAssembler.toModel(paymentUpdate);
        return ResponseEntity.status(HttpStatus.OK).body(entityModel);
    }


    //Anular un Pago
    @PatchMapping ("/{id}/anular")
    @Operation(summary = "Anular Pago", description = "Invalida un pago existente cambiando su estado a ANULADO")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pago anulado con éxito",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PaymentDetalleDTO.class))),
            @ApiResponse(responseCode = "404", description = "Pago no encontrado")
    })
    public ResponseEntity <EntityModel<PaymentDetalleDTO>> nullById (
            @Parameter(description = "ID de un Pago", required = true, example = "1")
            @PathVariable Long id) {
        PaymentDetalleDTO paymentVoid = paymentService.nullById(id);
        EntityModel<PaymentDetalleDTO> entityModel = paymentModelAssembler.toModel(paymentVoid);
        return ResponseEntity.status(HttpStatus.OK).body(entityModel);
    }
}
