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
public class PaymentDetalleDTO {
    private Long id;
    private Long ordenId;
    private double monto;
    private String metodo;
    private String estado;
    private String codigoTransaccion;
    private LocalDateTime fecha;
}
