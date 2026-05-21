package com.GameHub.models.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class DirectionDetalleDTO {
    private String comuna;
    private String ciudad;
    private String calle;
    private String numero;
}