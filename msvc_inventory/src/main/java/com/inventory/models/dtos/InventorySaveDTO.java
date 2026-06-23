package com.inventory.models.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class InventorySaveDTO {
    @NotNull(message = "El ID del producto es obligatorio")
    private Long productId;

    @NotNull(message = "El stock disponible es obligatorio")
    private Long stockDisponible;

    @NotNull(message = "El stock mínimo es obligatorio")
    private Long stockMinimo;
    @NotNull(message = "El stock reservado es obligatorio")
    private Long stockReservado;

    @NotBlank(message = "La ubicación es obligatoria")
    private String ubicacion;

    private List<MovimientoDetalleDTO> movimientosDTO;
}
