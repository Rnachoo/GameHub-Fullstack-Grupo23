package com.promotion.controllers;

import com.promotion.models.dtos.PromotionAplicarDescuentoDTO;
import com.promotion.models.dtos.PromotionDetalleDTO;
import com.promotion.models.dtos.PromotionSaveDTO;
import com.promotion.models.dtos.PromotionUpdateDateDTO;
import com.promotion.services.PromotionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/promociones")
@Validated
@Tag(
        name = "Promociones V1",
        description = "Métodos CRUD para la gestión de promociones, cupones y descuentos"
)
public class PromotionController {

    @Autowired
    private PromotionService promotionService;

    @GetMapping
    @Operation(
            summary = "Listado de todas las promociones",
            description = "Devuelve todas las promociones registradas en el sistema"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Operación exitosa"
    )
    public ResponseEntity<List<PromotionDetalleDTO>> findAll() {
        return ResponseEntity.status(HttpStatus.OK).body(promotionService.findAll());
    }

    @GetMapping("/current")
    @Operation(
            summary = "Listado de promociones vigentes",
            description = "Devuelve todas las promociones activas y vigentes según la fecha actual"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Promociones encontradas"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No existen promociones vigentes"
            )
    })
    public ResponseEntity<List<PromotionDetalleDTO>> findCurrent() {
        return ResponseEntity.status(HttpStatus.OK).body(promotionService.findCurrent());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Búsqueda de promoción por ID",
            description = "Devuelve los detalles de una promoción específica"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Promoción encontrada",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PromotionDetalleDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Promoción no encontrada"
            )
    })
    public ResponseEntity<PromotionDetalleDTO> findById(
            @Parameter(
                    description = "ID de la promoción a buscar",
                    required = true,
                    example = "1"
            )
            @PathVariable Long id
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(promotionService.findById(id));
    }

    @GetMapping("/codigo/{codigo}")
    @Operation(
            summary = "Búsqueda de promoción por código",
            description = "Devuelve una promoción utilizando su código promocional"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Promoción encontrada",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PromotionDetalleDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Código promocional no encontrado"
            )
    })
    public ResponseEntity<PromotionDetalleDTO> findByCodigo(
            @Parameter(
                    description = "Código de la promoción",
                    required = true,
                    example = "GAMEHUB20"
            )
            @PathVariable String codigo
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(promotionService.findByCodigo(codigo));
    }

    @PostMapping
    @Operation(
            summary = "Crear promoción",
            description = "Registra una nueva promoción o cupón de descuento"
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos de la promoción a registrar",
            required = true,
            content = @Content(
                    schema = @Schema(
                            implementation = PromotionSaveDTO.class
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Promoción creada correctamente"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos"
            )
    })
    public ResponseEntity<PromotionDetalleDTO> save(
            @Valid @RequestBody PromotionSaveDTO promotionSaveDTO
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(promotionService.save(promotionSaveDTO));
    }

    @PatchMapping("/{id}")
    @Operation(
            summary = "Desactivar promoción",
            description = "Desactiva una promoción registrada sin eliminarla físicamente"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Promoción desactivada correctamente"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Promoción no encontrada"
            )
    })
    public ResponseEntity<PromotionDetalleDTO> desactiveById(
            @Parameter(
                    description = "ID de la promoción a desactivar",
                    required = true,
                    example = "1"
            )
            @PathVariable Long id
    ) {
        PromotionDetalleDTO promotion = promotionService.desactiveById(id);
        return ResponseEntity.ok(promotion);
    }

    @PatchMapping("/{id}/date")
    @Operation(
            summary = "Actualizar fechas de promoción",
            description = "Actualiza la fecha de inicio y fecha de término de una promoción"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Fechas actualizadas correctamente"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Promoción no encontrada"
            )
    })
    public ResponseEntity<PromotionDetalleDTO> updateDate(
            @Parameter(
                    description = "ID de la promoción",
                    required = true,
                    example = "1"
            )
            @PathVariable Long id,
            @Valid @RequestBody PromotionUpdateDateDTO promotionUpdateDateDTO
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(promotionService.updateDate(id, promotionUpdateDateDTO));
    }

    @PostMapping("/{codigo}/aplicar")
    @Operation(
            summary = "Aplicar promoción a una orden",
            description = "Valida y aplica un descuento utilizando un código promocional sobre el total de una orden"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Promoción aplicada correctamente"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Promoción inválida o no aplicable"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Promoción no encontrada"
            )
    })
    public ResponseEntity<PromotionDetalleDTO> aplicarPromocion(
            @Parameter(
                    description = "Código promocional",
                    required = true,
                    example = "GAMEHUB20"
            )
            @PathVariable("codigo") String codigo,

            @RequestBody PromotionAplicarDescuentoDTO aplicarDescuentoDTO,

            @Parameter(
                    description = "Monto total de la orden antes del descuento",
                    required = true,
                    example = "150000"
            )
            @RequestParam("totalOrden") Double totalOrden
    ) {

        PromotionDetalleDTO promotionAplicada =
                promotionService.aplicarPromocion(
                        codigo,
                        aplicarDescuentoDTO,
                        totalOrden
                );

        return ResponseEntity.ok(promotionAplicada);
    }
}