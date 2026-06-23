package com.user.models.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class UserDetalleDTO {
    private Long id;
    private String nombreUser;
    private String email;
    private String telefono;
    private String estado;
    private List<DirectionDetalleDTO> directionsDTO; //Anidamiento de las direcciones
}
