package com.GameHub.models.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
@Getter
@Setter
@ToString
@NoArgsConstructor
public class PromotionDTO {
    private Long id;
    private String codigo;
    private double valor;
    private String tipo;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private double montoMinimo;
    private Long usosMaximos;
    private String estado;

}