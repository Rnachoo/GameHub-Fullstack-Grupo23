package com.gamehub.msvc_product.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
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