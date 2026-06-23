package com.auth.models.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class AuthSaveDTO {
    @NotBlank(message = "El nombre de cuenta es obligatorio")
    private String nombreCuenta;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Debe proporcionar un formato de email válido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria y no puede estar vacía")
    private String password;

    @NotBlank(message = "El rol es obligatorio")
    private String rol;

    private String estado;
}
