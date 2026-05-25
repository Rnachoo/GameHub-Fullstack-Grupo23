package com.GameHub.models.dtos;

import lombok.*;

import java.math.BigDecimal;
import jakarta.validation.constraints.*;

@Getter
@Setter
@ToString
@NoArgsConstructor

public class ProductRequestDTO {

    @NotBlank(message = "El nombre del producto es obligatorio y no puede estar vacío.")
    private String nombre;

    @NotBlank(message = "La marca del producto es obligatoria.")
    private String marca;

    private String modelo;

    @NotNull(message = "El precio es obligatorio.")
    @Positive(message = "El precio debe ser un número mayor que cero.")
    private Long precio;

    @NotNull(message = "El ID de la categoria es obligatorio.")
    private Long categoriaId;

    private String descripcion;
}