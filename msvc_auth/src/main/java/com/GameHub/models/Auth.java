package com.GameHub.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name="cuentas")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Auth {

    @NotBlank(message = "El nombre de cuenta es obligatorio")
    @Column(unique = true)
    private String nombreCuenta;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cuenta_id")
    private Long Id;

    @NotBlank(message = "El campo de correo no puede ser vacio")
    @Email(message = "El campo de correo tiene que tener el formato de correo")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "La constraseña es obligatorio y no puede estar vacia")
    private String passwordHash;


    private String rol;
    private String estado;
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

}
