package com.GameHub.models.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class AuthDetalleDTO {
    private Long id;
    private String email;
    private String estado;
    private String rol;
    private UserDTO user;//Anidamiento de la info del MSVC User
    private String token;
}
