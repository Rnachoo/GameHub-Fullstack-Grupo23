package com.GameHub.models.dtos;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class PromotionSaveDTO {
    @NotBlank(message = "El codigo de la promocion es obligatorio")
    private String codigo;

    @NotNull(message = "El valor no puede ser 0")
    private double valor;

    @NotBlank(message = "El tipo de promocion es obligatorio")
    private String tipo;

    @NotNull(message = "La fecha de inicio es oblogatiria")
    private LocalDateTime fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDateTime fechaFin;

    private double montoMinimo;
    private Long usosMaximos;
    private String estado;

    private Long categoryId;
}

