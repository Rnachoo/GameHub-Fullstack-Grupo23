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
public class AuthUpdatePasswordDTO {

    @NotBlank(message = "La constraseña es obligatorio y no puede estar vacia")
    private String password;
}

