package com.category.models.dtos;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class CategorySaveDTO {

    @NotBlank(message = "El nombre de cuenta es obligatorio")
    private String nombreCategory;

    String descripcion;
    String estado;


}
