package com.promotion.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name="promociones")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class    Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "promocion_id")
    private Long id;

    @NotBlank(message = "El codigo de la promocion es obligatorio")
    @Column(unique = true)
    private String codigo;

    @NotNull(message = "El valor no puede ser 0")
    private double valor;

    @NotBlank(message = "El tipo de promocion es obligatorio")
    private String tipo;
    @NotNull(message = "La fecha de inicio es oblogatiria")
    private LocalDateTime fechaInicio;
    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDateTime fechaFin;
    private double montoMinimo;
    private Long usosMaximos;
    private String estado;
}
