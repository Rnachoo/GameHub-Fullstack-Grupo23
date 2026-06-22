package com.GameHub.controllers;

import com.GameHub.assemblers.ReviewModelAssembler;
import com.GameHub.models.dtos.ResenaRequestDTO;
import com.GameHub.models.dtos.ResenaResponseDTO;
import com.GameHub.models.dtos.ResenaUpdateDTO;
import com.GameHub.services.ReviewService;
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

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/resenas")
@Validated
@Tag(name = "Reseñas V1", description = "Métodos CRUD para la gestión de reseñas de productos")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ReviewModelAssembler reviewModelAssembler;

    @GetMapping("/{id}")
    @Operation(summary = "Buscar reseña por ID", description = "Devuelve los detalles de una reseña específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reseña encontrada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResenaResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Reseña no encontrada")
    })
    public ResponseEntity<EntityModel<ResenaResponseDTO>> findById(
            @Parameter(description = "ID de la reseña a buscar", required = true, example = "1")
            @PathVariable Long id) {
        EntityModel<ResenaResponseDTO> entityModel = reviewModelAssembler.toModel(reviewService.getResenaById(id));
        return ResponseEntity.status(HttpStatus.OK).body(entityModel);
    }

    @GetMapping("/producto/{productoId}")
    @Operation(summary = "Buscar reseñas por producto", description = "Devuelve todas las reseñas de un producto específico")
    @ApiResponse(responseCode = "200", description = "Operación exitosa")
    public ResponseEntity<CollectionModel<EntityModel<ResenaResponseDTO>>> findByProductoId(
            @Parameter(description = "ID del producto", required = true, example = "1")
            @PathVariable Long productoId) {
        List<EntityModel<ResenaResponseDTO>> entityModels = reviewService.getResenasByProductoId(productoId)
                .stream()
                .map(reviewModelAssembler::toModel)
                .toList();
        CollectionModel<EntityModel<ResenaResponseDTO>> collectionModel = CollectionModel.of(
                entityModels,
                linkTo(methodOn(ReviewController.class).findByProductoId(productoId)).withSelfRel()
        );
        return ResponseEntity.status(HttpStatus.OK).body(collectionModel);
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Buscar reseñas por usuario", description = "Devuelve todas las reseñas de un usuario específico")
    @ApiResponse(responseCode = "200", description = "Operación exitosa")
    public ResponseEntity<CollectionModel<EntityModel<ResenaResponseDTO>>> findByUsuarioId(
            @Parameter(description = "ID del usuario", required = true, example = "1")
            @PathVariable Long usuarioId) {
        List<EntityModel<ResenaResponseDTO>> entityModels = reviewService.getResenasByUsuarioId(usuarioId)
                .stream()
                .map(reviewModelAssembler::toModel)
                .toList();
        CollectionModel<EntityModel<ResenaResponseDTO>> collectionModel = CollectionModel.of(
                entityModels,
                linkTo(methodOn(ReviewController.class).findByUsuarioId(usuarioId)).withSelfRel()
        );
        return ResponseEntity.status(HttpStatus.OK).body(collectionModel);
    }

    @PostMapping
    @Operation(summary = "Crear una reseña", description = "Registra una nueva reseña de un producto comprado")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos de la reseña a crear", required = true,
            content = @Content(schema = @Schema(implementation = ResenaRequestDTO.class))
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Reseña creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<EntityModel<ResenaResponseDTO>> save(@Valid @RequestBody ResenaRequestDTO requestDTO) {
        EntityModel<ResenaResponseDTO> entityModel = reviewModelAssembler.toModel(reviewService.createResena(requestDTO));
        return ResponseEntity.status(HttpStatus.CREATED).body(entityModel);
    }

    @PatchMapping("/{id}/update")
    @Operation(summary = "Actualizar una reseña", description = "Actualiza el comentario o puntuación de una reseña")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reseña actualizada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Reseña no encontrada")
    })
    public ResponseEntity<EntityModel<ResenaResponseDTO>> update(
            @Parameter(description = "ID de la reseña", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody ResenaUpdateDTO requestDTO) {
        EntityModel<ResenaResponseDTO> entityModel = reviewModelAssembler.toModel(reviewService.updateResena(id, requestDTO));
        return ResponseEntity.status(HttpStatus.OK).body(entityModel);
    }

    @PatchMapping("/{id}/moderar")
    @Operation(summary = "Moderar una reseña", description = "Modera o desactiva una reseña del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reseña moderada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Reseña no encontrada")
    })
    public ResponseEntity<EntityModel<ResenaResponseDTO>> moderar(
            @Parameter(description = "ID de la reseña a moderar", required = true, example = "1")
            @PathVariable Long id) {
        EntityModel<ResenaResponseDTO> entityModel = reviewModelAssembler.toModel(reviewService.moderarResena(id));
        return ResponseEntity.status(HttpStatus.OK).body(entityModel);
    }
}