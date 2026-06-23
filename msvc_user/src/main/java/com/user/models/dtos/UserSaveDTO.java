package com.user.models.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class UserSaveDTO {

    @NotBlank(message = "El nombre de usuario es obligatorio")
    private String nombreUser;

    @NotBlank(message = "El número de telefono es obligatorio")
    private String telefono;

    @NotBlank(message = "El campo de correo no puede ser vacio")
    @Email(message = "El campo de correo tiene que tener el formato de correo")
    private String email;

    private List<DirectionDetalleDTO> directionsDTO; //Anidamiento de las direcciones

}



