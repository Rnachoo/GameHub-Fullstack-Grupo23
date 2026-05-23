package com.GameHub.models.dtos;
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
public class AuthLoginDTO {
    @NotBlank(message = "El campo de correo no puede ser vacio")
    @Email(message = "Debe proporcionar un formato de email válido")
    private String email;
    @NotBlank(message = "La constraseña es obligatorio y no puede estar vacia")
    private String password;
}
