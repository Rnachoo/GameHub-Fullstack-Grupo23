package com.GameHub.models.dtos;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@NoArgsConstructor

public class ProductResponseDTO {
    private Long id;
    private String nombre;
    private String marca;
    private String modelo;
    private BigDecimal precio;
    private Long categoriaId;
    private String descripcion;
    private boolean estado;
}