package com.GameHub.models.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
@Getter
@Setter
@ToString
@NoArgsConstructor
public class CategoryDetalleDTO {

    private String nombreCategory;
    private Long id;
    private String estado;
    private String descripcion;
}
