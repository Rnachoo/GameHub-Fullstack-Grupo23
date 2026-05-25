package com.GameHub.models.dtos;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor

public class DespachoRequestDTO {

    @NotNull(message = "El ID de la orden es obligatorio")
    private Long ordenId;

    @NotNull(message = "El ID del usuario es obligatorio")
    private Long usuarioId;

    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;

    @NotBlank(message = "El transportista es obligatorio")
    private String transportista;

    private String tracking;

}