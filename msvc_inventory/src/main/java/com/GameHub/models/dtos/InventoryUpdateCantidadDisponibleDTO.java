package com.GameHub.models.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class InventoryUpdateCantidadDisponibleDTO {

    @NotNull(message = "El stock disponible es obligatorio")
    private Long stockDisponible;

    @NotNull(message = "El stock reservado es obligatorio")
    private Long stockReservado;

    @NotNull(message = "El stock mínimo es obligatorio")
    private Long stockMinimo;

    @NotBlank(message = "La ubicación es obligatoria")
    private String ubicacion;


}
