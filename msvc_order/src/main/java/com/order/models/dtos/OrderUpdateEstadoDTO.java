package com.order.models.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class OrderUpdateEstadoDTO {
    @NotBlank(message = "El nuevo estado no puede estar vacío")
    private String estado;

}
