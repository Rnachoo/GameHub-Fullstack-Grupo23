package com.inventory.models.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class MovimientoDetalleDTO {
    private Long id;
    private Long productId;
    private String tipo;
    private Long cantidad;
    private LocalDateTime fecha;
}
