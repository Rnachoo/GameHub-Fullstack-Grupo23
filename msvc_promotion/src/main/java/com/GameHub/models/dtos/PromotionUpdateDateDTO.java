package com.GameHub.models.dtos;

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
public class PromotionUpdateDateDTO {

    @NotNull(message = "La fecha de inicio es oblogatiria")
    private LocalDateTime fechaInicio;
    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDateTime fechaFin;
}
