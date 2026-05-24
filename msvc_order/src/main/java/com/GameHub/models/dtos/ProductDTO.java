package com.GameHub.models.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ProductDTO {
        private Long id;
        private String marca;
        private String modelo;
        private Long precio;
        private Long categoryId;
        private String descripcion;
        private String estado;
    }
