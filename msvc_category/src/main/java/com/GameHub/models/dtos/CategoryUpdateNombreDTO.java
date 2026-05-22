package com.GameHub.models.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class CategoryUpdateNombreDTO {
    @NotBlank(message = "El nombre de cuenta es obligatorio")
    private String nombreCategory;
}
