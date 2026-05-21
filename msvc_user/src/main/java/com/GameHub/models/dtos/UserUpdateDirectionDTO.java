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
public class UserUpdateDirectionDTO {
    @NotBlank(message = "El campo de Comuna no puede ser vacio")
    private String comuna;

    @NotBlank(message = "El campo de Ciudad no puede ser vacio")
    private String ciudad;

    @NotBlank(message = "El campo de Calle no puede ser vacio")
    private String calle;

    @NotBlank(message = "El campo de Numero no puede ser vacio")
    private String numero;

}
