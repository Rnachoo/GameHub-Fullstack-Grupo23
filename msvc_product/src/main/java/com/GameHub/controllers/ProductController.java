package com.GameHub.controllers;

import com.GameHub.models.dtos.ProductRequestDTO;
import com.GameHub.models.dtos.ProductResponseDTO;
import com.GameHub.services.ProductService;
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
@RequestMapping("/api/v1/productos")
@Validated
@Tag(
        name = "Productos V1",
        description = "Métodos CRUD para la gestión del catálogo de productos de GameHub Store"
)
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    @Operation(
            summary = "Listado de productos",
            description = "Devuelve todos los productos registrados en el catálogo"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Operación exitosa"
    )
    public ResponseEntity<List<ProductResponseDTO>> findAll() {
        return ResponseEntity.status(HttpStatus.OK).body(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Búsqueda de producto por ID",
            description = "Devuelve los detalles de un producto específico"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Producto encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProductResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Producto no encontrado"
            )
    })
    public ResponseEntity<ProductResponseDTO> findById(
            @Parameter(
                    description = "ID del producto a buscar",
                    required = true,
                    example = "1"
            )
            @PathVariable Long id
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.getProductById(id));
    }

    @PostMapping
    @Operation(
            summary = "Crear producto",
            description = "Registra un nuevo producto en el catálogo"
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos del producto a registrar",
            required = true,
            content = @Content(
                    schema = @Schema(
                            implementation = ProductRequestDTO.class
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Producto creado correctamente"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos"
            )
    })
    public ResponseEntity<ProductResponseDTO> save(
            @Valid @RequestBody ProductRequestDTO requestDTO
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.createProduct(requestDTO));
    }

    @PatchMapping("/{id}")
    @Operation(
            summary = "Desactivar producto",
            description = "Desactiva un producto sin eliminarlo físicamente de la base de datos"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Producto desactivado correctamente"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Producto no encontrado"
            )
    })
    public ResponseEntity<ProductResponseDTO> deactivate(
            @Parameter(
                    description = "ID del producto a desactivar",
                    required = true,
                    example = "1"
            )
            @PathVariable Long id
    ) {
        productService.deactivateProduct(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/{id}/update")
    @Operation(
            summary = "Actualizar producto",
            description = "Actualiza la información comercial y técnica de un producto registrado"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Producto actualizado correctamente"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Producto no encontrado"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos"
            )
    })
    public ResponseEntity<ProductResponseDTO> update(
            @Parameter(
                    description = "ID del producto a actualizar",
                    required = true,
                    example = "1"
            )
            @PathVariable Long id,

            @Valid @RequestBody ProductRequestDTO requestDTO
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(productService.updateProduct(id, requestDTO));
    }
}