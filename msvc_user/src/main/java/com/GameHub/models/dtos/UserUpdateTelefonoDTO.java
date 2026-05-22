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
public class UserUpdateTelefonoDTO {

    @NotBlank(message = "El número de telefono es obligatorio")
    private String telefono;
}
